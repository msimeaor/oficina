package io.github.msimeaor.aplicacao.helper;

import jakarta.validation.GroupSequence;
import jakarta.validation.groups.Default;

@GroupSequence({Default.class, EmailValidationGroup.class})
public interface EmailValidationGroup { }
