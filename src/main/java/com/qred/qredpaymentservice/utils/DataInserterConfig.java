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
            var client1 = getClient("199711281234", "hassan.sheikha@email.com", "Hassan", "Some Address 12");
            var savedClient = clientRepository.save(client1);
            var newContract1 = new Contract();
            newContract1.setContractNumber("12345");
            newContract1.setStatus("ACTIVE");
            newContract1.setPayments(List.of());
            newContract1.setClient(savedClient);
            contractRepository.save(newContract1);

            var newContract2 = new Contract();
            newContract2.setContractNumber("54321");
            newContract2.setStatus("ACTIVE");
            newContract2.setPayments(List.of());
            newContract2.setClient(savedClient);
            contractRepository.save(newContract2);
        };
    }

    private Client getClient(String id, String email, String name, String address) {
        var newClient1 = new Client();
        newClient1.setId(id);
        newClient1.setEmail(email);
        newClient1.setName(name);
        newClient1.setAddress(address);
        return newClient1;
    }
}
