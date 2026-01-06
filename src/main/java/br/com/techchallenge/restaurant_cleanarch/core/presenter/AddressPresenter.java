package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.Address;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.AddressOutput;

public class AddressPresenter {
    private AddressPresenter() {}


    public static AddressOutput toOutput(Address address) {
        return new AddressOutput(address.getStreet(), address.getNumber(), address.getCity(), address.getState(), address.getZipCode(), address.getComplement());
    }
}
