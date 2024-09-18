package com.dmdev.validator;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;

class CreateSubscriptionValidatorTest {

    private final CreateSubscriptionValidator validator = CreateSubscriptionValidator.getInstance();

    @Test
    void shouldPassValidation() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertFalse(actualResult.hasErrors());
    }

    @Test
    void invalidUserId() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("Andrei")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(100);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void invalidName(String name) {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name(name)
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(101);
    }

    @Test
    void invalidProvider() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Andrei")
                .provider("fake")
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(102);
    }

    @Test
    void invalidExpirationDate() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.GOOGLE.name())
                .expirationDate(Instant.now().minus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(1);
        assertThat(actualResult.getErrors().get(0).getCode()).isEqualTo(103);
    }

    @Test
    void invalidUserIdNameProviderExpirationDate() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(null)
                .name("")
                .provider("fake")
                .expirationDate(Instant.now().minus(5, ChronoUnit.DAYS))
                .build();

        ValidationResult actualResult = validator.validate(createSubscriptionDto);

        assertThat(actualResult.getErrors()).hasSize(4);
        List<Integer> errorCodes = actualResult.getErrors().stream()
                .map(Error::getCode)
                .toList();
        assertThat(errorCodes).contains(100, 101, 102, 103);
    }
}