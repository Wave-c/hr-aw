package com.wave.recommendations_service.components;

import org.springframework.stereotype.Component;

import com.wave.dtos.ApplicationDto;
import com.wave.dtos.VacancyDto;

@Component
public class TextBuilder {

    public String buildVacancyText(VacancyDto v) {
        return String.join(" ",
                v.title(),
                v.description(),
                String.join(" ", v.tags()),
                String.join(" ", v.formats())
        );
    }

    public String buildCandidateText(ApplicationDto a) {
        return String.join(" ",
                a.resumeText(),
                a.coverLetter() != null ? a.coverLetter() : ""
        );
    }
}
