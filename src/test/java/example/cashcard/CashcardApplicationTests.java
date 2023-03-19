package example.cashcard;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import java.net.URI;

//this will allow our test to make requests to our application
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CashcardApplicationTests {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    public void shouldReturnACashCardWhenDataIsSaved() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/99", String.class);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        DocumentContext documentContext = JsonPath.parse(response.getBody());
        Number id = documentContext.read("$.id");
        assertThat(id).isNotNull();
        assertThat(id).isEqualTo(99);

        Number amount = documentContext.read("$.amount");
        assertThat(amount).isNotNull();
        assertThat(amount).isEqualTo(123.45);

    }

    @Test
    void shouldNotReturnACashCardWithAnUnknownId() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cashcards/1000", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody()).isBlank();
    }

    @Test
    void shouldCreateANewCashCard() {
        // creating a cash card
        CashCard newCashCard = new CashCard(null, 250.00);

        // saving response from create operation         sending post request          sending card  expecting void to be a body
        ResponseEntity<Void> createResponse = restTemplate.postForEntity("/cashcards", newCashCard, Void.class);

        // checking if status code of previously saved response is 201
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        // retrieving uri from previously saved response
        URI locationOfNewCashCard = createResponse.getHeaders().getLocation();

        // now we have to check if received uri is correct by sending a get request using it
        ResponseEntity<String> getResponse = restTemplate.getForEntity(locationOfNewCashCard, String.class);
        // if it was correct, our getResponse should have status code of OK because we managed to find created resource
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);

        // already familiar document context and JsonPath
        DocumentContext documentContext = JsonPath.parse(getResponse.getBody());

        Number id = documentContext.read("$.id");
        Double amount = documentContext.read("$.amount");

        assertThat(id).isNotNull();
        assertThat(amount).isEqualTo(250.00);

    }
}








































