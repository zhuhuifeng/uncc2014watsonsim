package edu.uncc.cs.watsonsim;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.concurrent.TimeUnit.MINUTES;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Wrapper logger
 * 
 * Loggers already allow many modules to log to many places.
 * But we need each module to log to some (but not all) places. So basically,
 * want to pass around a fancy many-to-many channel. 
 * 
 * @author Sean
 *
 */
public class Log {
	private Consumer<String> listener;
	//private final Optional<Log> parent;
	private final Class<?> speaker;
	private final long start;
	
	private enum Level {ERROR, WARNING, INFO, DEBUG};
	
	public static final Log NIL = new Log(Object.class, x->{});
	
	// Start a root logger
	public Log(Class<?> speaker, Consumer<String> listener) {
		//this.parent = Optional.empty();
		this.speaker = speaker;
		this.start = System.currentTimeMillis();
		this.listener = listener;
	}
	
	// Start a child logger
	public Log(Class<?> speaker, Log parent) {
		//this.parent = Optional.of(parent);
		this.speaker = speaker;
		this.start = parent.start;
		this.listener = parent.listener;
	}
	
	/**
	 * Make a new writable subchannel.
	 */
	public Log kid(Class<?> speaker) {
		return new Log(speaker, this);
	}
	
	public void setListener(Consumer<String> listener) {
		this.listener = listener;
	}
	
	/**
	 * Push some notifications. Listeners may lose interest.
	 */
	private void push(String content, Level level) {
		listener.accept(String.format("%.2f [%s %s] %s",
				(System.currentTimeMillis()-start) / 1000.0,
				level.name(),
				speaker.getSimpleName(),
				content));
	}
	
	public void error(String message) {
		push(message, Level.ERROR);
	}
	
	public void warn(String message) {
		push(message, Level.WARNING);
	}
	
	public void info(String message) {
		push(message, Level.INFO);
	}
	
	public void debug(String message) {
		push(message, Level.DEBUG);
	}
}