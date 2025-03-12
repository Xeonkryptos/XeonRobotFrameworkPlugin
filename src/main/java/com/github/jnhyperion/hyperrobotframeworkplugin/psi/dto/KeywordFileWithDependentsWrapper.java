package com.github.jnhyperion.hyperrobotframeworkplugin.psi.dto;

import com.github.jnhyperion.hyperrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record KeywordFileWithDependentsWrapper(@NotNull KeywordFile keywordFile, @NotNull Collection<KeywordFile> dependents) {}
