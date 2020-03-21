package starter.spock;

import spock.lang.Specification
import spock.lang.Unroll
import starter.spock.AddingMachine

public class AddingMachineSpec extends Specification {

	def "add operation"() {
		given:
		def AddingMachine machine = new AddingMachine()
		def result = machine.add(5, 6)

		expect:
		result == 11
		result == 11
	}
	
	def "lists comparison"() {
		given:
		def list = [1, 2, 3, 4, 5]
		
		when:
		list.remove(0)
		
		then:
		list == [2, 3, 4, 5]
	}
	
	def "data pipes"(int a, int b) {
		expect:
		2 * a == b
			
		where:
		a << [1, 2, 3, 4, 5]
		b << [2 , 4, 6, 8, 10]
	}
	
	// Unroll with dynamic names per test case
	@Unroll
	def "data table sum #a^#b = #c"(int a, int b, int c) {
		expect: 
		Math.pow(a, b) == c
		
		where:
		a   |  b  || c
		1   |  1  || 1
		200 |  2  || 40000
		2   |  3  || 8
	}
	
	def "stubbing method"() {
		
		given:
		AddingMachine machine = Mock()
		machine.same(1, 1) >> false
		machine.same(_, _) >> true
		
		expect:
		machine.same(1, 1) == false	// mocked specific params
		machine.same(3, 5) == true	// mocked any params
		
		and:
		machine.add(2, 3) == 0	// default value from mock
	}
		
	def "Stubbing method with several responses"() {
		
		given: "Mocked machine for specific case - must be defined before any '_' case"
		AddingMachine machine = Mock()
		machine.same(1, 1) >> false 
		
		and: "Mocked machine for any other cases with multiple results"
		machine.same(_, _) >>> [true, false, true]
		
		expect: "check specific and other cases"
		machine.same(1, 1) == false // specific mocked case, not changin order of 'any' case
		
		machine.same(3, 5) == true	// first response
		machine.same(3, 5) == false // second response
		machine.same(3, 5) == true // third mock response
		machine.same(3, 5) == true // last known mock response
		machine.same(3, 5) == true // last known mock response
		
		machine.same(1, 1) == false // specific mocked case
		
		and: "method that gives default mocked result"
		machine.add(2, 3) == 0	// default value from mock
	}
	
	def "Stubbing method with exceptions"() {
		
		given:
		AddingMachine machine = Mock()
		machine.same(1, 1) >> { throw new RuntimeException("Doesn't work") }
		machine.same(2, 2) >> true
		
		when: "call with 1 and 1"
		machine.same(1, 1)
		
		then: "throw exception for this call"
		def ex = thrown(RuntimeException)
        ex.message == "Doesn't work"
		
		expect: "OK on call with valid params"
		machine.same(2, 2) == true
	}
	
	def "Stubbing machine exceptions - multi stub"() {
		given:
		AddingMachine machine = Mock()
		machine.same(2, 2) >> true >> { throw new RuntimeException("Doesn't work") }
		
		expect: "OK on first call"
		machine.same(2, 2) == true

		when: "second call should throw exception"
		machine.same(2, 2)
		
		then: "exception is expected on second call"
		def ex = thrown(RuntimeException)
		ex.message == "Doesn't work"
	}
	
	
	def "invocation count check"() {
		given:
		AddingMachine machine = Mock(AddingMachine)
		
		when:
		machine.execute(1, "run")
		machine.execute(2, "run")
		machine.execute(3, "run-2")

		then:
		2 * machine.execute(_, "run")
		1 * machine.execute(3, "run-2")

		// not working, because checks already done:
		// 1 * machine.execute(3, "run-2")
		// 3 * machine.execute(_, _)
	}
	
	def "invocation count check with parameter negation"() {
		given:
		AddingMachine machine = Mock(AddingMachine)
		
		when:
		machine.execute(1, 'run')
		machine.execute(2, 'run')
		machine.execute(3, 'run-2')

		then:
		1 * machine.execute(_, !'run')
	}
	
	def "Stub object"() {
		given:
		AddingMachine machine = Stub(AddingMachine) {
			add(_, _) >> 100
		}
		
		when:
		def result = machine.add(2, 3)
		
		then:
		result == 100
	}

	
	def "Spy object"() {
		given: "spy with stubbed add() method"
		AddingMachine machine = Spy(AddingMachine, constructorArgs: []) 
		// stubbed single method
		machine.add(_, _) >> 100
		
		expect: "Stubbed method result"
		machine.add(1, 1) == 100
		
		and: "Real method result"
		machine.same(4, 4) == true 
	}
	
	def "assertion method checking more conditions"() {
		given:
		AddingMachine machine = new AddingMachine()
		
		when:
		def result = machine.add(3, 3)
		
		then:
		result == 6
		checkResult(result)
	}

	// example of helper method to check more conditions
	void checkResult(result) {
		assert result > 0
		assert result == 6
	}
	
}
