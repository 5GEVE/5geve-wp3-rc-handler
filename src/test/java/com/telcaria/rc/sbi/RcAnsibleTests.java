package com.telcaria.rc.sbi;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
public class RcAnsibleTests {

	@Test
	@Order(1)
	public void executeAnsibleHelloWorldTemplate() {
		// Make sure you have this repository in your $HOME:
		// https://github.com/5GEVE/5geve-rc
		String home = "/home/telcaria";
		String path = home + "/5g-eve/github/5geve-rc/hello_world/ansible";
		String ipAddress = "10.9.8.204";
		String user = "user";
		String password = "root";
		String param = "5GEVE";

		int exitVal = -1;
		ProcessBuilder hostProcessBuilder = new ProcessBuilder("bash", "-c", "touch hosts ; echo \"src_server ansible_host=" +
						ipAddress + " ansible_user=" + user + " ansible_ssh_pass=" + password + " param=" + param +
						"\" | tee -a hosts ; touch ansible_output; ansible-playbook -i hosts hello_world.yml");
		hostProcessBuilder.directory(new File(path));

		try {
			Process process = hostProcessBuilder.start();

			StringBuilder output = new StringBuilder();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				output.append(s + "\n");
			}
			exitVal = process.waitFor();
			if (exitVal == 0) {
				log.info("Success!");
				log.info(output.toString());
			} else {
				log.info("Failure!");
				log.info(output.toString());
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		Assertions.assertTrue(exitVal == 0);

	}

	@Test
	@Order(2)
	public void executeAnsibleCommandTemplate() {
		// Make sure you have this repository in your $HOME:
		// https://github.com/5GEVE/5geve-rc
		String home = "/home/telcaria";
		String path = home + "/5g-eve/github/5geve-rc/execute_script/ansible";
		String ipAddress = "10.9.8.204";
		String user = "user";
		String password = "root";
		String script = "echo hello world";

		int exitVal = -1;
		ProcessBuilder hostProcessBuilder = new ProcessBuilder("bash", "-c", "[ ! -e hosts ] || rm hosts && touch hosts && " +
				"echo \"server ansible_host=" + ipAddress + " ansible_user=" + user + " ansible_ssh_pass=" + password + " ansible_become_pass=" + password +
				"\" | tee -a hosts && touch ansible_output && ansible-playbook -i hosts execute_script.yml -e 'script=\"" +
				script + "\"'");
		hostProcessBuilder.directory(new File(path));

		try {
			Process process = hostProcessBuilder.start();

			StringBuilder output = new StringBuilder();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				output.append(s + "\n");
			}
			exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
			} else {
				System.out.println("Failure!");
				System.out.println(output);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		Assertions.assertTrue(exitVal == 0);

	}

	@Test
	@Order(3)
	public void executeAnsibleCommandTemplateWithScript() {
		// Make sure you have this repository in your $HOME:
		// https://github.com/5GEVE/5geve-rc
		String home = "/home/telcaria";
		String path = home + "/5g-eve/github/5geve-rc/execute_script/ansible";
		String script = "[ ! -e hosts ] || rm hosts && touch hosts && echo \"server ansible_host=10.9.8.204 ansible_user=user ansible_ssh_pass=root ansible_become_pass=root\" | tee -a hosts && touch ansible_output && ansible-playbook -i hosts execute_script.yml -e 'script=\"echo hello world\"'";

		int exitVal = -1;
		ProcessBuilder hostProcessBuilder = new ProcessBuilder("bash", "-c", script);
		hostProcessBuilder.directory(new File(path));

		try {
			Process process = hostProcessBuilder.start();

			StringBuilder output = new StringBuilder();
			BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String s = null;
			while ((s = stdInput.readLine()) != null) {
				output.append(s + "\n");
			}
			exitVal = process.waitFor();
			if (exitVal == 0) {
				System.out.println("Success!");
				System.out.println(output);
			} else {
				System.out.println("Failure!");
				System.out.println(output);
			}
		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}

		Assertions.assertTrue(exitVal == 0);

	}


}
