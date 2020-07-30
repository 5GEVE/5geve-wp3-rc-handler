package com.telcaria.rc.sbi;

import static com.telcaria.rc.TestCommonData.EXECUTE_COMMAND_GENERATED_ANSIBLE_COMMAND_1;
import static com.telcaria.rc.TestCommonData.EXECUTE_COMMAND_TCB_PING_1;
import static com.telcaria.rc.TestCommonData.EXECUTE_COMMAND_TCB_LS_1;
import static com.telcaria.rc.TestCommonData.INSTALL_FILEBEAT_GENERATED_ANSIBLE_COMMAND_1;
import static com.telcaria.rc.TestCommonData.INSTALL_FILEBEAT_TCB_1;
import static com.telcaria.rc.TestCommonData.SLEEP_GENERATED_ANSIBLE_COMMAND_1;
import static com.telcaria.rc.TestCommonData.SLEEP_TCB_1;
import static org.awaitility.Awaitility.await;

import com.telcaria.rc.core.service.RCService;
import com.telcaria.rc.nbi.enums.ExecutionStatus;
import com.telcaria.rc.nbi.wrappers.ExecutionWrapper;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class RcAnsibleTests {

	@Autowired
	SBIProvider sbiProvider;

	@Autowired
	RCService rcService;

	@Value("${test.ansible.playbooks.path}")
	private String playbooksPath = "/Users/aitor/telcaria-code/";

	@Test
	@Order(1)
	void testScriptParsing() {

		AnsibleCommand ansibleCommand = sbiProvider.parseScript(INSTALL_FILEBEAT_TCB_1);
		ansibleCommand.setId("819c5d41-e9f6-4e10-a8b1-4df27a8c031d");
		String command = ansibleCommand.generateAnsibleCommand(playbooksPath+"5geve-rc/filebeat_data_shipper_test/ansible");
		assert command.equals(INSTALL_FILEBEAT_GENERATED_ANSIBLE_COMMAND_1);

		ansibleCommand = sbiProvider.parseScript(SLEEP_TCB_1);
		ansibleCommand.setId(UUID.randomUUID().toString());
		command = ansibleCommand.generateAnsibleCommand(null);
		assert command.equals(SLEEP_GENERATED_ANSIBLE_COMMAND_1);

		ansibleCommand = sbiProvider.parseScript(EXECUTE_COMMAND_TCB_PING_1);
		ansibleCommand.setId("8600f80a-b523-4de2-a1ae-2f2deda57746");
		command = ansibleCommand.generateAnsibleCommand(playbooksPath+"5geve-rc/execute_script/ansible");
		assert command.equals(EXECUTE_COMMAND_GENERATED_ANSIBLE_COMMAND_1);

	}

	@Test
	@Order(2)
	void sleepCommandTest() {
		ExecutionWrapper executionWrapper = new ExecutionWrapper();
		executionWrapper.setExecScript(SLEEP_TCB_1);

		String execId = rcService.loadExecution(executionWrapper);
		executionWrapper.setExecutionId(execId);
		ExecutionWrapper executionWrapperPersisted = rcService.getExecution(execId);
		assert executionWrapperPersisted.getExecScript().equals(SLEEP_TCB_1);

		sbiProvider.start(executionWrapper);
		await().atMost(10, TimeUnit.SECONDS).until(() -> rcService.getExecutionStatus(execId).equals(ExecutionStatus.COMPLETED));
	}

	@Test
	@Order(3)
	void ExecuteCommandTest() {
		ExecutionWrapper executionWrapper = new ExecutionWrapper();
		executionWrapper.setExecScript(EXECUTE_COMMAND_TCB_LS_1);

		String execId = rcService.loadExecution(executionWrapper);
		executionWrapper.setExecutionId(execId);
		ExecutionWrapper executionWrapperPersisted = rcService.getExecution(execId);
		assert executionWrapperPersisted.getExecScript().equals(EXECUTE_COMMAND_TCB_LS_1);

		sbiProvider.start(executionWrapper);
		await().atMost(10, TimeUnit.SECONDS).until(() -> rcService.getExecutionStatus(execId).equals(ExecutionStatus.COMPLETED));
	}
}
