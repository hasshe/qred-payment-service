package com.qred.qredpaymentservice.utils;

import com.qred.qredpaymentservice.repository.ClientRepository;
import com.qred.qredpaymentservice.repository.ContractRepository;
import com.qred.qredpaymentservice.repository.entities.Client;
import com.qred.qredpaymentservice.repository.entities.Contract;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
@Profile("local")
public class DataInserterConfig {

    @Bean
    public CommandLineRunner populateData(ClientRepository clientRepository, ContractRepository contractRepository) {
        return args -> {
            var client1 = getClient("1", "hassan.sheikha@email.com", "Hassan", "Some Address 12");
            var client2 = getClient("2", "test.testsson@email.com", "Test", "Some Address 121");
            var savedClient1 = clientRepository.save(client1);
            var savedClient2 = clientRepository.save(client2);
            var newContract1 = getContract("12345", "ACTIVE", savedClient1);
            var newContract2 = getContract("54321", "ACTIVE", savedClient1);
            var newContract3 = getContract("67890", "INACTIVE", savedClient2);
            contractRepository.save(newContract1);
            contractRepository.save(newContract2);
            contractRepository.save(newContract3);
        };
    }

    private Client getClient(String id, String email, String name, String address) {
        var client = new Client();
        client.setId(id);
        client.setEmail(email);
        client.setName(name);
        client.setAddress(address);
        return client;
    }

    private Contract getContract(String contractNumber, String status, Client client) {
        var contract = new Contract();
        contract.setContractNumber(contractNumber);
        contract.setStatus(status);
        contract.setPayments(List.of());
        contract.setClient(client);
        return contract;
    }
}
