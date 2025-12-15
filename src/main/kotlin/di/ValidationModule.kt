package kz.idl.di

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator
import org.koin.dsl.module

val validationModule = module {
    single<Validator> {
        Validation.byDefaultProvider()
            .configure()
            .messageInterpolator(ParameterMessageInterpolator())
            .buildValidatorFactory()
            .validator
    }
}
