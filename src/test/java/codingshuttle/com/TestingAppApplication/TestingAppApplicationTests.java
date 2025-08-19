package codingshuttle.com.TestingAppApplication;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Slf4j
class TestingAppApplicationTests {

	@Test
	void contextLoads() {
	}

	@BeforeAll
	static void runBeforeAll(){
		log.info("testing started .....");
	}

	@AfterAll
	static void runAfterAll(){
		log.info("testing ended .....");
	}

	@BeforeEach
	void runBeforeEach(){
		log.info("running before each test case");
	}

	@AfterEach
	void runAfterEach(){
		log.info("running after each test case");
	}

	@Test
//	@Disabled
	void testDisable(){
		log.info("Testing disable");
	}

	@Test
//	@DisplayName("Krishnakant")
	void testDisplayName(){
		log.info("Testing display name");
	}

	@Test
	void performingAddition(){
		int a = 5;
		int b = 3;

		int result = addingTwoNumbers(a, b);
//		Assertions.assertEquals(8, result);
//		Assertions.assertThat(result)
//						.isEqualTo(8)
//						.isCloseTo(9, Offset.offset(1));

		assertThat("Apple")
				.isEqualTo("Apple")
				.startsWith("App")
				.endsWith("le")
				.hasSize(5);

		log.info("Test case Passed......");

	}
	@Test
	void testTwoNumbers(){
		int a = 5;
		int b = 0;

		double result = divideTwoNumbers(a, b);
	}

	int addingTwoNumbers(int a, int b){
		return a+b;
	}

	double divideTwoNumbers(int a, int b){
		try{
			return a/b;
		} catch (ArithmeticException e) {
			log.error("Arithmetic exception occured: ", e.getLocalizedMessage());
			throw new ArithmeticException(e.getLocalizedMessage());
		}

	}

}
