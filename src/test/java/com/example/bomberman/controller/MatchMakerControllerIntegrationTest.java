package com.example.bomberman.controller;

import com.example.bomberman.Client;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MatchMakerControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;


    @Test
    public void join() throws Exception {

        /*for (int i = 0; i < 4; i++) {
            new Thread(() -> {*/
                /*String str = "0";
                String name = String.valueOf(Math.random());
                try {
                    str = mockMvc.perform(post("/matchmaker/join")
                                    .content("name=" + name)
                                    .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                            .andExpect(status().isOk())
                            .andReturn().getResponse().getContentAsString();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                System.out.println(str);
                Client client = new Client(port + "", str, name);
                client.close();*/
       /*     }).start();
        }
        Thread.sleep(5000);*/
    }

    @Test
    public void greetingShouldReturnDefaultMessage() throws Exception {
        for (int i = 0; i < 4; i++) {
            int finalI = i;
            new Thread(() -> {
                Thread.currentThread().setName("Client Test " + finalI);
                String name = String.valueOf(Math.random());
                String gameId = this.restTemplate.postForObject("http://localhost:{port}/matchmaker/join?name={name}",
                        null,
                        String.class,
                        port,
                        name);
                Client client = new Client(port + "", gameId, name);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                client.close();
            }).start();

            //assertThat().contains("Hello, World");
        }
        Thread.sleep(10000);
    }
}
