package com.dmdev.mapper;

import com.dmdev.dto.CreateSubscriptionDto;
import com.dmdev.entity.Provider;
import com.dmdev.entity.Status;
import com.dmdev.entity.Subscription;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class CreateSubscriptionMapperTest {

    private final CreateSubscriptionMapper createSubscriptionMapper = CreateSubscriptionMapper.getInstance();

    @Test
    void map() {
        CreateSubscriptionDto createSubscriptionDto = CreateSubscriptionDto.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.APPLE.name())
                .expirationDate(Instant.now().plus(5, ChronoUnit.DAYS))
                .build();

        Subscription actualResult = createSubscriptionMapper.map(createSubscriptionDto);

        Subscription expectedResult = Subscription.builder()
                .userId(1)
                .name("Andrei")
                .provider(Provider.APPLE)
                .expirationDate(actualResult.getExpirationDate())
                .status(Status.ACTIVE)
                .build();

        assertEquals(expectedResult, actualResult);
    }
}