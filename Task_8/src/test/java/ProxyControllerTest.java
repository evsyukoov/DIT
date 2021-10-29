import app.ProxyController;
import app.SpringServer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = SpringServer.class)
@AutoConfigureMockMvc
public class ProxyControllerTest {

    @Autowired
    private ProxyController proxyController;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void controllerTest() {
        assertThat(proxyController).isNotNull();
    }

    @Test
    public void testGetMethod() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(content().json(getExpectedContent("get.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void testPostMethod() throws Exception {
        mockMvc.perform(post("/"))
                .andExpect(content().json(getExpectedContent("post.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void testPutMethod() throws Exception {
        mockMvc.perform(put("/"))
                .andExpect(content().json(getExpectedContent("put.json")))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteMethod() throws Exception {
        mockMvc.perform(delete("/"))
                .andExpect(content().json(getExpectedContent("delete.json")))
                .andExpect(status().isOk());
    }

    private String getExpectedContent(String fileName) throws IOException {
        return String.join("", Files.readAllLines(
                Paths.get(
                        this.getClass().getClassLoader()
                                .getResource(fileName).getPath())));
    }

}
