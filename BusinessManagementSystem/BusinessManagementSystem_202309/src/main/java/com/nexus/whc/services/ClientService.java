package com.nexus.whc.services;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nexus.whc.form.ClientForm;
import com.nexus.whc.repository.ClientRepository;

@Service
public class ClientService {

	private final ClientRepository clientRepository;

	@Autowired
	public ClientService(ClientRepository clientRepository) {
		this.clientRepository = clientRepository;
	}

	public List<Map<String, Object>> findAllClient() {
		return clientRepository.findAllClient();
	}

	public int insertClient(ClientForm clientForm) {
		return clientRepository.insertClient(clientForm);
	}

	public Map<String, Object> findClientById(Integer clientId) {
		return clientRepository.findClientById(clientId);
	}

	private String formatTime(Object time) {

		if (time == null) {
			return "";
		}

		return time.toString().substring(0, 5);
	}

	public ClientForm getClientFormById(Integer clientId) {

		Map<String, Object> client = clientRepository.findClientById(clientId);

		ClientForm clientForm = new ClientForm();

		clientForm.setClientId(((Number) client.get("client_id")).intValue());
		clientForm.setClientName((String) client.get("client_name"));
		clientForm.setOpenTime(formatTime(client.get("open_time")));
		clientForm.setCloseTime(formatTime(client.get("close_time")));
		clientForm.setWorkingTime(String.valueOf(client.get("working_time")));

		clientForm.setRest1Start(formatTime(client.get("rest1_start")));
		clientForm.setRest1End(formatTime(client.get("rest1_end")));
		clientForm.setRest2Start(formatTime(client.get("rest2_start")));
		clientForm.setRest2End(formatTime(client.get("rest2_end")));
		clientForm.setRest3Start(formatTime(client.get("rest3_start")));
		clientForm.setRest3End(formatTime(client.get("rest3_end")));
		clientForm.setRest4Start(formatTime(client.get("rest4_start")));
		clientForm.setRest4End(formatTime(client.get("rest4_end")));
		clientForm.setRest5Start(formatTime(client.get("rest5_start")));
		clientForm.setRest5End(formatTime(client.get("rest5_end")));
		clientForm.setRest6Start(formatTime(client.get("rest6_start")));
		clientForm.setRest6End(formatTime(client.get("rest6_end")));

		clientForm.setAdjustRestTimeStart(
				formatTime(client.get("adjust_rest_time_start")));
		clientForm.setAdjustRestTimeEnd(
				formatTime(client.get("adjust_rest_time_end")));

		clientForm.setComment((String) client.get("comment"));

		return clientForm;
	}

	public int updateClient(ClientForm clientForm) {
		return clientRepository.updateClient(clientForm);
	}

	public int deleteClients(List<Integer> clientIds) {
		return clientRepository.deleteClients(clientIds);
	}

	public List<Map<String, Object>> searchClients(
			String clientId, String clientName, int page) {

		return clientRepository.searchClients(clientId, clientName, page);
	}

	public int countClients(String clientId, String clientName) {
		return clientRepository.countClients(clientId, clientName);
	}

	public boolean existsClient(Integer clientId, String clientName) {

		return clientRepository.existsClient(clientId, clientName);
	}

	public boolean existsActiveClient(Integer clientId) {

		return clientRepository.existsActiveClient(clientId);
	}
}
