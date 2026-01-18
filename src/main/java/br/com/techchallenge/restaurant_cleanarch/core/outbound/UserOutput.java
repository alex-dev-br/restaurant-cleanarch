package br.com.techchallenge.restaurant_cleanarch.core.outbound;

import java.util.*;

public record UserOutput(
        UUID id,
        String name,
        String email,
        AddressOutput address,
        UserTypeOutput userType
) {}
