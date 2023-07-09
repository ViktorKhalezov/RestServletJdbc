package com.example.rest_servlet_jdbc.servlet_tests;

import com.example.rest_servlet_jdbc.dto.TeacherDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import org.apache.hc.client5.http.classic.methods.HttpDelete;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpResponse;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TeacherServletTest {

    private static final WireMockServer wireMockServer = new WireMockServer();

    private final String URL = "/RestServletJdbc/teacher/";

    private static String HOST;

    private static int PORT;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    static void startServer() {
        Properties properties = new Properties();
        try (InputStream fileInputStream = CourseServletTest.class.getClassLoader().getResourceAsStream("application.properties")) {
            properties.load(fileInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HOST = properties.getProperty("host");
        PORT = Integer.parseInt(properties.getProperty("port"));
        WireMock.configureFor(HOST, PORT);
        wireMockServer.start();
    }

    @AfterAll
    static void stopServer() {
        wireMockServer.stop();
    }

    @Test
    void doGetTest() throws IOException {
        TeacherDto teacherDto = new TeacherDto();
        teacherDto.setId(2L);
        teacherDto.setFirstname("Vitaly");
        teacherDto.setLastname("Kolosov");
        teacherDto.setFaculty("Technical");
        Set<String> courses = new HashSet<>();
        courses.add("Programming");
        courses.add("Mathematics");
        teacherDto.setCourses(courses);

        stubFor(get(urlEqualTo(URL + 2))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody(objectMapper.writeValueAsString(teacherDto))));


        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet("http://" + HOST + ":" + PORT + URL + 2);
        HttpResponse httpResponse = httpClient.execute(request);


        verify(getRequestedFor(urlEqualTo(URL + 2)));
        assertEquals(200, httpResponse.getCode());
        assertEquals("application/json", httpResponse.getFirstHeader("Content-Type").getValue());
    }

    @Test
    void doPostTest() throws IOException {
        stubFor(post(urlEqualTo(URL)).willReturn(status(201)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost request = new HttpPost("http://" + HOST + ":" + PORT + URL);
        HttpResponse response = httpClient.execute(request);

        verify(postRequestedFor(urlEqualTo(URL)));
        assertEquals(201, response.getCode());
    }

    @Test
    void doPutTest() throws IOException {
        stubFor(put(urlEqualTo(URL + 2)).willReturn(status(200)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPut request = new HttpPut("http://" + HOST + ":" + PORT + URL + 2);
        HttpResponse response = httpClient.execute(request);

        verify(putRequestedFor(urlEqualTo(URL + 2)));
        assertEquals(200, response.getCode());
    }

    @Test
    void doDeleteTest() throws IOException {
        stubFor(delete(urlEqualTo(URL + 1))
                .willReturn(aResponse()
                        .withStatus(200)));

        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpDelete request = new HttpDelete("http://" + HOST + ":" + PORT + URL + 1);
        HttpResponse httpResponse = httpClient.execute(request);

        verify(deleteRequestedFor(urlEqualTo(URL + 1)));
        assertEquals(200, httpResponse.getCode());
    }

}
