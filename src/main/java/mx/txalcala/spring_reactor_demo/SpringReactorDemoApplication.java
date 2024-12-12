package mx.txalcala.spring_reactor_demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
public class SpringReactorDemoApplication implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(SpringReactorDemoApplication.class);
	private static List<String> dishes = new ArrayList<>();

	public static void main(String[] args) {
		SpringApplication.run(SpringReactorDemoApplication.class, args);
	}

	public void createMono() {
		Mono<Integer> m1 = Mono.just(42);
		// Para poder ejecutar y emitir el contenido de una fuente no bloqueante debe
		// pasar por un proceso de subscripción
		// Los subscribe son especificados de manera ímplicita solo cuando trabajamos
		// con la consola
		// m1.subscribe(number -> System.out.println("Numero : " + number));
		m1.subscribe(number -> log.info("Numero {}", number));
		Mono<String> m2 = Mono.just("Hello students");
		m2.subscribe(log::info);
	}

	public void createFlux() {
		Flux<String> fx1 = Flux.fromIterable(dishes);
		// fx1.subscribe(x -> log.info("Dish: " + x));

		// collectList: pasar de un flux a un mono
		fx1.collectList()
				.map(list -> String.join(" - ", list))
				.subscribe(list -> log.info(list.toString()));

	}

	// RX OPERATORS
	public void m1DoOnNext() {
		Flux<String> fx1 = Flux.fromIterable(dishes);
		fx1.doOnNext(x -> log.info("Element: " + x)).subscribe();
	}

	public void m2Map() {
		Flux<String> fx1 = Flux.fromIterable(dishes);
		// fx1.map(x -> x.toUpperCase()).subscribe(x -> log.info(x));
		// fx1.map(String::toUpperCase).subscribe(log::info);
		fx1.map(x -> x.toUpperCase());
		fx1.subscribe(x -> log.info(x));
	}

	public void m3FlatMap() {
		// Mono.just("dalila").map(x -> 33).subscribe(e -> log.info("Data: " + e));
		// Mono<Mono<T>>
		// Mono.just("martin").map(x -> Mono.just(33)).subscribe(e -> log.info("Data: "
		// + e));
		Mono.just("martin").flatMap(x -> Mono.just(33)).subscribe(e -> log.info("Data: " + e));
	}

	public void m4Range() {
		Flux<Integer> fx1 = Flux.range(0, 10); // 0-9
		fx1.map(e -> e + 1).subscribe(e -> log.info("Data: " + e));
	}

	public void m5DelayElements() throws InterruptedException {
		Flux.range(0, 20)
				.delayElements(Duration.ofSeconds(1))
				.doOnNext(x -> log.info("Element: " + x))
				.subscribe();
		Thread.sleep(10000);
	}

	public void m6ZipWith() {
		List<String> clients = new ArrayList<>();
		clients.add("Client 1");
		clients.add("Client 2");
		// clients.add("Client 3");

		Flux<String> fx1 = Flux.fromIterable(clients);
		Flux<String> fx2 = Flux.fromIterable(dishes);

		fx1.zipWith(fx2, (c, d) -> c + " - " + d).subscribe(log::info);
	}

	public void m7Merge() {
		List<String> clients = new ArrayList<>();
		clients.add("Client 1");
		clients.add("Client 2");

		Flux<String> fx1 = Flux.fromIterable(clients);
		Flux<String> fx2 = Flux.fromIterable(dishes);
		Mono<String> m1 = Mono.just("Txalcala");

		fx1.doOnNext(e -> {
			throw new ArithmeticException("BAD OPERATION");
		}).subscribe();

		Flux.merge(fx1, fx2, m1, m1, fx2).subscribe(log::info);
	}

	public void m8Filter() {
		Flux<String> fx1 = Flux.fromIterable(dishes);

		fx1.filter(e -> e.startsWith("Ce"))
				.subscribe(log::info);
	}

	public void m9TakeLast() {
		Flux<String> fx1 = Flux.fromIterable(dishes);
		fx1.takeLast(6).subscribe(log::info);
	}

	public void m10Take() {
		Flux<String> fx1 = Flux.fromIterable(dishes);
		fx1.take(6).subscribe(log::info);
	}

	public void m11DefaultIfEmpty() {
		dishes = new ArrayList<>();
		Flux<String> fx1 = Flux.fromIterable(dishes);
		// Cada elemento agregale la palabra Dish: "Dish: + e"
		fx1.map(e -> "Dish: " + e)
				.defaultIfEmpty("EMPTY FLUX")
				.subscribe(log::info);
	}

	public void m12Error() {
		Flux<String> fx1 = Flux.fromIterable(dishes);

		fx1.doOnNext(e -> {
			throw new ArithmeticException("BAD OPERATION");
		})
				// .onErrorMap(e -> new Exception(e.getMessage()))
				.onErrorReturn("ERROR, TRY AGAIN")
				.subscribe(log::info);

	}

	@Override
	public void run(String... args) throws Exception {
		dishes.add("Ceviche");
		dishes.add("Estofado de pollo");
		dishes.add("Mariscos");
		// createMono();
		// createFlux();
		// m1DoOnNext();
		// m2Map();
		// m3FlatMap();
		// m4Range();
		// m5DelayElements();
		// m6ZipWith();
		// m7Merge();
		// m8Filter();
		// m9TakeLast();
		// m10Take();
		// m11DefaultIfEmpty();
		m12Error();
	}
}
