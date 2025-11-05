package bdd.steps;

import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.example.paymentservice.domain.model.Webhook;
import org.example.paymentservice.infrastructure.adapter.persistence.adapter.WebhookRepositoryAdapter;
import org.example.paymentservice.infrastructure.adapter.persistence.repository.WebhookJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class PaymentSteps {

    @Autowired
    private WebhookRepositoryAdapter webhookRepositoryAdapter;

    @Autowired
    private WebhookJpaRepository webhookJpaRepository;
    private UUID webhookId;

    @Before
    public void setup() {
        // i always clean database before each scenario
        // because of trauma
        webhookJpaRepository.deleteAll();
    }

    @Given("given a webhook endpoint is configured with the URL {urlString}")
    public void registerWebhookUrl(String urlString) {
        this.webhookId =  webhookRepositoryAdapter.save(Webhook.builder()
                        .description("Some description")
                        .url(urlString)
                        .secret("Some secret")
                        .isActive(true)
                        .url(urlString)
                        .build()).getId();
    }

    @Given("I have a valid payment details")
    public void paymentDetails() { // NOP
    }

    @When("I submit the payment")
    public void postPaymentDetails() {
        // NOP
    }

    @Then("the payment should be processed successfully")
    public void paymentProcessedSucessFully() {
        // NOP
    }

    @Then("the card number should be masked in the response")
    public void creditCardNumberMasked() {
        // NOP
    }

    @Then("a payment event should be recorded in the outbox table")
    public void paymentEventSaved() {
        // NOP
    }

    @Then("a payment event should be published in the message queue")
    public void paymentEventPublished() {
        // NOP
    }

    @Then("a confirmation message should be sent to the webhook registered")
    public void confirmationMessageSent() {
        // NOP
    }




}
