package com.telcaria.rc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.CharStreams;
import com.telcaria.rc.core.wrappers.msno.CpProtocolInfo;
import com.telcaria.rc.core.wrappers.msno.ExtCpInfo;
import com.telcaria.rc.core.wrappers.msno.IpAddresses;
import com.telcaria.rc.core.wrappers.msno.NsInstance;
import com.telcaria.rc.core.wrappers.msno.VnfInstance;
import feign.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@Slf4j
class RcApplicationTests {

	@Autowired
	MsnoClientTest msnoClientTest;

	@Test
	void contextLoads() {
	}

	@Test
	void testMsno() {

		String ipAddressesString = "nil";

		Response nsResponse = msnoClientTest.getNsInstance("1.0", "application/json",
																											 "application/json", "79097c9c-b96e-40c4-8824-e71ef742c2e0",
																											 true, false);

		if (nsResponse != null && nsResponse.status() == 200) {
			log.info("nsInstance received: {}", nsResponse.body().toString());
			ObjectMapper objectMapper = new ObjectMapper();
			NsInstance nsInstance = null;
			try {
				nsInstance = objectMapper.readValue(nsResponse.body().toString(), NsInstance.class);
				List<String> ipAddresses = parseNsInstanceToListIpAddresses(nsInstance);
				ipAddressesString = parseIpAddressListToString(ipAddresses);
				log.info("VNF IP addresses received: {}", ipAddressesString);
			} catch (JsonProcessingException e) {
				log.error("Error parsing JSON response from MSNO");
				e.printStackTrace();
			}
		} else {
			log.warn("nsInstance is null");
		}
	}

	@Test
	void testMsno2() {

		String ipAddressesString = "nil";

		String nsResponse = "{\"id\":\"79097c9c-b96e-40c4-8824-e71ef742c2e0\",\"nsInstanceDescription\":\"NFV NS instance for experiment 378d9a16-57d2-458a-9761-61e325077cad\",\"nsInstanceName\":\"NS_exp_378d9a16-57d2-458a-9761-61e325077cad\",\"nsState\":\"INSTANTIATED\",\"nsdId\":\"0afe98b3-a15b-3ae3-93f3-1e67cd7bac30\",\"nsdInfoId\":\"\",\"vnfInstance\":[{\"id\":\"267c4e02-0e59-4e73-9c95-b3793df095cc\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.68\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:23:71:2b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_mgmt\",\"id\":\"cp_remote_controller_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.0.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:89:bf:6f\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_data\",\"id\":\"cp_remote_controller_data\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.21\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:22:94:0e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_ext_mobile\",\"id\":\"cp_remote_controller_ext_mobile\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.186.20\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:5c:f1:e3\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_user\",\"id\":\"cp_remote_controller_user\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"ce542fc9-3e6f-4baf-8698-c5da7dfaf8dc\",\"vnfdVersion\":\"\"},{\"id\":\"8158565f-a760-4bc3-a8cf-af151ce07b3d\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.62\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:58:3a:4b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_mgmt\",\"id\":\"cp_dg_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.41\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:e9:67:52\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_in\",\"id\":\"cp_dg_traffic_in\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.90.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:30:d1:3e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_out\",\"id\":\"cp_dg_traffic_out\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"396d1b6b-331b-4dd7-b48e-376517d3654a\",\"vnfdVersion\":\"\"}]}";

		log.info("nsInstance received: {}", nsResponse);
		ObjectMapper objectMapper = new ObjectMapper();
		NsInstance nsInstance = null;
		try {
			nsInstance = objectMapper.readValue(nsResponse, NsInstance.class);
			List<String> ipAddresses = parseNsInstanceToListIpAddresses(nsInstance);
			ipAddressesString = parseIpAddressListToString(ipAddresses);
			log.info("VNF IP addresses received: {}", ipAddressesString);
		} catch (JsonProcessingException e) {
			log.error("Error parsing JSON response from MSNO");
			e.printStackTrace();
		}
	}

	@Test
	void testMsnoCompare() {

		String ipAddressesString = "nil";

		Response nsResponse = msnoClientTest.getNsInstance("1.0", "application/json",
																											 "application/json", "79097c9c-b96e-40c4-8824-e71ef742c2e0",
																											 true, false);

		String nsResponseString = "{\"id\":\"79097c9c-b96e-40c4-8824-e71ef742c2e0\",\"nsInstanceDescription\":\"NFV NS instance for experiment 378d9a16-57d2-458a-9761-61e325077cad\",\"nsInstanceName\":\"NS_exp_378d9a16-57d2-458a-9761-61e325077cad\",\"nsState\":\"INSTANTIATED\",\"nsdId\":\"0afe98b3-a15b-3ae3-93f3-1e67cd7bac30\",\"nsdInfoId\":\"\",\"vnfInstance\":[{\"id\":\"267c4e02-0e59-4e73-9c95-b3793df095cc\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.68\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:23:71:2b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_mgmt\",\"id\":\"cp_remote_controller_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.0.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:89:bf:6f\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_data\",\"id\":\"cp_remote_controller_data\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.21\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:22:94:0e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_ext_mobile\",\"id\":\"cp_remote_controller_ext_mobile\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.186.20\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:5c:f1:e3\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_user\",\"id\":\"cp_remote_controller_user\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"ce542fc9-3e6f-4baf-8698-c5da7dfaf8dc\",\"vnfdVersion\":\"\"},{\"id\":\"8158565f-a760-4bc3-a8cf-af151ce07b3d\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.62\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:58:3a:4b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_mgmt\",\"id\":\"cp_dg_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.41\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:e9:67:52\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_in\",\"id\":\"cp_dg_traffic_in\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.90.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:30:d1:3e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_out\",\"id\":\"cp_dg_traffic_out\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"396d1b6b-331b-4dd7-b48e-376517d3654a\",\"vnfdVersion\":\"\"}]}";

		if (nsResponse != null && nsResponse.status() == 200) {
			log.info("nsInstance received: {}", nsResponse.body().toString());
			log.info("nsInstanceString received: {}", nsResponseString);

			boolean compareBoth = nsResponseString.equals(nsResponse.body().toString());

			log.info("Comparison: {}", compareBoth);
		}
	}

	@Test
	void testMsno3() {

		String ipAddressesString = "nil";

		Response nsResponse = msnoClientTest.getNsInstance("1.0", "application/json",
																											 "application/json", "79097c9c-b96e-40c4-8824-e71ef742c2e0",
																											 true, false);
		String nsResponseString = "{\"id\":\"79097c9c-b96e-40c4-8824-e71ef742c2e0\",\"nsInstanceDescription\":\"NFV NS instance for experiment 378d9a16-57d2-458a-9761-61e325077cad\",\"nsInstanceName\":\"NS_exp_378d9a16-57d2-458a-9761-61e325077cad\",\"nsState\":\"INSTANTIATED\",\"nsdId\":\"0afe98b3-a15b-3ae3-93f3-1e67cd7bac30\",\"nsdInfoId\":\"\",\"vnfInstance\":[{\"id\":\"267c4e02-0e59-4e73-9c95-b3793df095cc\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.68\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:23:71:2b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_mgmt\",\"id\":\"cp_remote_controller_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.0.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:89:bf:6f\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_data\",\"id\":\"cp_remote_controller_data\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.21\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:22:94:0e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_ext_mobile\",\"id\":\"cp_remote_controller_ext_mobile\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.186.20\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:5c:f1:e3\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_remote_controller_user\",\"id\":\"cp_remote_controller_user\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"ce542fc9-3e6f-4baf-8698-c5da7dfaf8dc\",\"vnfdVersion\":\"\"},{\"id\":\"8158565f-a760-4bc3-a8cf-af151ce07b3d\",\"instantiatedVnfInfo\":{\"extCpInfo\":[{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.80.62\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:58:3a:4b\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_mgmt\",\"id\":\"cp_dg_mgmt\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"10.50.160.41\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:e9:67:52\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_in\",\"id\":\"cp_dg_traffic_in\"},{\"cpProtocolInfo\":[{\"ipOverEthernet\":{\"addressRange\":{\"maxAddress\":\"\",\"minAddress\":\"\"},\"ipAddresses\":[{\"addresses\":[\"192.168.90.3\"],\"type\":\"IPV4\"}],\"macAddress\":\"fa:16:3e:30:d1:3e\"},\"layerProtocol\":\"IP_OVER_ETHERNET\"}],\"cpdId\":\"cp_dg_traffic_out\",\"id\":\"cp_dg_traffic_out\"}],\"flavourId\":\"\",\"vnfState\":\"STARTED\"},\"instantiationState\":\"INSTANTIATED\",\"vimId\":\"c5060169-8305-4ce0-9b37-9eaca59c42f4\",\"vnfPkgId\":\"\",\"vnfProductName\":\"\",\"vnfProvider\":\"\",\"vnfSoftwareVersion\":\"\",\"vnfdId\":\"396d1b6b-331b-4dd7-b48e-376517d3654a\",\"vnfdVersion\":\"\"}]}";

		if (nsResponse != null && nsResponse.status() == 200) {
			log.info("nsInstance received: {}", nsResponseString);
			ObjectMapper objectMapper = new ObjectMapper();
			NsInstance nsInstance = null;
			try {
				String responseStream = nsResponse.body().toString();
				log.info("nsInstanceStream received: {}", responseStream);

				boolean compareBoth = nsResponseString.equals(responseStream);

				log.info("Comparison: {}", compareBoth);

				nsInstance = objectMapper.readValue(responseStream, NsInstance.class);
				List<String> ipAddresses = parseNsInstanceToListIpAddresses(nsInstance);
				ipAddressesString = parseIpAddressListToString(ipAddresses);
				log.info("VNF IP addresses received: {}", ipAddressesString);
			} catch (JsonProcessingException e) {
				log.error("Error parsing JSON response from MSNO");
				e.printStackTrace();
			}
		} else {
			log.warn("nsInstance is null");
		}
	}

	private List<String> parseNsInstanceToListIpAddresses(NsInstance nsInstance) {
		List<String> ipAddresses = new ArrayList<>();

		for(VnfInstance vnfInstance : nsInstance.getVnfInstance()) {
			for(ExtCpInfo extCpInfo : vnfInstance.getInstantiatedVnfInfo().getExtCpInfo()) {
				for(CpProtocolInfo cpProtocolInfo : extCpInfo.getCpProtocolInfo()) {
					for(IpAddresses ipAddresses1 : cpProtocolInfo.getIpOverEthernet().getIpAddresses()) {
						for(String address : ipAddresses1.getAddresses()) {
							ipAddresses.add(address);
						}
					}
				}
			}

		}

		return ipAddresses;
	}

	private String parseIpAddressListToString(List<String> ipAddresses) {
		String ipAddressesString = "";

		if (ipAddresses.size() > 0) {
			for (String s : ipAddresses) {
				ipAddressesString = ipAddressesString.concat(s + ",");
			}
			// Remove the last char
			ipAddressesString = ipAddressesString.substring(0, ipAddressesString.length() - 1);
		} else {
			ipAddressesString = "nil";
		}

		return ipAddressesString;
	}

}
