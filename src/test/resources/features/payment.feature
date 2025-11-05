Feature: Payment Processing
  As a payment service
  I want to process payments so that he user can make paymetns

  Background: given a webhook endpoint is configured with the URL "https:example.com/webhook"

  Scenario: Sucessfull Payment
    Given I have a valid payment details
    When I submit the payment
    Then the payment should be processed successfully
    And the card number should be masked in the response
    And a payment event should be recorded in the outbox table
    And a payment event should be published in the message queue
    And a confirmation message should be sent to the webhook registered