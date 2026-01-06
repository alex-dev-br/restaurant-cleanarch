package br.com.techchallenge.restaurant_cleanarch.core.presenter;

import br.com.techchallenge.restaurant_cleanarch.core.domain.model.OpeningHours;
import br.com.techchallenge.restaurant_cleanarch.core.outbound.OpeningHoursOutput;

public class OpeningHoursPresenter {
    private OpeningHoursPresenter() {}

    public static OpeningHoursOutput toOutput(OpeningHours openingHours) {
        return new OpeningHoursOutput(openingHours.getId(), openingHours.getDayOfWeek(), openingHours.getOpenHour(), openingHours.getCloseHour());
    }
}
