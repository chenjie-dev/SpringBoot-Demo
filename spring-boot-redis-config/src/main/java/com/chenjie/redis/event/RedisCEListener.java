package com.chenjie.redis.event;

import io.lettuce.core.RedisException;
import io.lettuce.core.RedisURI;
import io.lettuce.core.resource.SocketAddressResolver;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisNode;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.scheduling.annotation.Async;
import reactor.core.publisher.Mono;

import java.net.SocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import static com.chenjie.redis.constant.RedisStarterConstant.DETECT_DELAY;
import static com.chenjie.redis.constant.RedisStarterConstant.DETECT_PERIOD;


/**
 * Redis Connection Error Listener
 */
@Slf4j
public class RedisCEListener {
    private final LettuceConnectionFactory factory;
    private final Environment env;
    public static final AtomicBoolean isHealthy = new AtomicBoolean(true);
    private final byte[] TEST_KEY = "REDIS_CONNECTION_TEST_KEY".getBytes(StandardCharsets.UTF_8);
    private final byte[] TEST_VALUE = "REDIS_CONNECTION_TEST_VALUE".getBytes(StandardCharsets.UTF_8);

    public RedisCEListener(LettuceConnectionFactory factory, Environment env) {
        this.factory = factory;
        this.env = env;
    }

    /**
     * 监听redis连接异常事件
     *
     * @param event
     */
    @Async
    @EventListener(classes = RedisConnectionErrorEvent.class)
    public void onApplicationEvent(RedisConnectionErrorEvent event) {
        Throwable e = event.getE();
        if (!(e instanceof DataAccessException || e instanceof RedisException)) {
            log.warn("{} is not an instance of RedisException, do nothing with it.", e.getClass());
            return;
        }
        log.warn("received redis connection exception event, source: {}, message: {}", event.getSource(), event.getMsg() == null ? e.getMessage() : event.getMsg(), e);
        isHealthy.set(false);
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new DetectHandler());
                    }

                    @Override
                    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
                        log.error("exception occurs, shutdown channel", cause);
                        ctx.channel().close();
                    }
                });

        RedisClusterConfiguration redisClusterConfiguration = factory.getClusterConfiguration();
        if (redisClusterConfiguration != null) {
            Set<RedisNode> clusterNodes = redisClusterConfiguration.getClusterNodes();
            SocketAddressResolver socketAddressResolver = factory.getClientResources().socketAddressResolver();
            Set<SocketAddress> socketAddressSet = clusterNodes.stream()
                    .map(node -> socketAddressResolver.resolve(new RedisURI(node.getHost(), node.getPort() == null ? 6379 : node.getPort(), Duration.ofSeconds(1))))
                    .collect(Collectors.toSet());
            long delay = env.getProperty(DETECT_DELAY, Long.class, 3000L);
            long period = env.getProperty(DETECT_PERIOD, Long.class, 3000L);
            for (SocketAddress socketAddress : socketAddressSet) {
                Mono<SocketAddress> supplier = Mono.just(socketAddress);
                RedisHeartbeatDetector heartbeatDetector = getHeartbeatDetector(bootstrap, new NioEventLoopGroup(), supplier, delay, period);
                if (!heartbeatDetector.active) {
                    continue;
                }
                heartbeatDetector.run();
            }
        }
    }

    private RedisHeartbeatDetector getHeartbeatDetector(Bootstrap bootstrap, EventLoopGroup detectors, Mono<SocketAddress> socketAddressSupplier,
                                                        long delay, long period) {
        return new RedisHeartbeatDetector(bootstrap, detectors, socketAddressSupplier, delay, period);

    }

    /**
     * redis健康状态探测器
     */
    class RedisHeartbeatDetector {
        private final Bootstrap bootstrap;
        private final EventLoopGroup detectors;
        private final Mono<SocketAddress> socketAddressSupplier;

        private final AtomicBoolean scheduled = new AtomicBoolean(false);
        private final long initialDelay;
        private final long period;
        private volatile boolean active = true;

        public RedisHeartbeatDetector(Bootstrap bootstrap, EventLoopGroup detectors, Mono<SocketAddress> socketAddressSupplier, long initialDelay, long period) {
            this.bootstrap = bootstrap;
            this.detectors = detectors;
            this.socketAddressSupplier = socketAddressSupplier;
            this.initialDelay = initialDelay;
            this.period = period;
        }

        public void run() {
            if (scheduled.get()) {
                return;
            }
            log.info("heartbeat detector started.");
            if (detectors.isTerminated()) {
                log.warn("detectors are inactive for now!");
                active = false;
                return;
            }
            scheduled.set(true);
            socketAddressSupplier.subscribe(this::doConnect);
        }

        private void doConnect(SocketAddress remoteAddress) {
            ScheduledFuture<?> scheduledFuture = detectors.scheduleAtFixedRate(() -> {
                log.debug("fixed delay command runs");
                try {
                    ChannelFuture future = bootstrap.connect(remoteAddress);
                    future.addListener(f -> {
                        if (f.cause() != null) {
                            log.error("network is unavailable. " + f.cause().getMessage());
                            f.cancel(true);
                        } else {
                            try (RedisConnection connection = factory.getConnection()) {
                                connection.setEx(TEST_KEY, 60000L, TEST_VALUE);
                            } catch (Exception e) {
                                log.error("network is available but command cannot be executed successfully due to {}! ", e.getMessage());
                                f.cancel(true);
                                return;
                            }
                            log.info("network and redis cluster are both available for now.");
                            isHealthy.compareAndSet(false, true);
                            scheduled.compareAndSet(true, false);
                            detectors.shutdownGracefully();
                        }
                    });
                } catch (Exception e) {
                    log.error("network detecting task failed.");
                }
            }, initialDelay, period, TimeUnit.MILLISECONDS);
            scheduledFuture.addListener(f -> {
                if (f.cause() != null) {
                    log.warn("scheduled task failed: {}", f.cause().getMessage());
                    f.cancel(true);
                }
            });
        }

        public Bootstrap getBootstrap() {
            return bootstrap;
        }

        public EventLoopGroup getDetectors() {
            return detectors;
        }

        public Mono<SocketAddress> getSocketAddressSupplier() {
            return socketAddressSupplier;
        }

        public AtomicBoolean getScheduled() {
            return scheduled;
        }

        public long getInitialDelay() {
            return initialDelay;
        }

        public long getPeriod() {
            return period;
        }

        public boolean isActive() {
            return active;
        }
    }
}
