package dev.xeonkryptos.xeonrobotframeworkplugin.psi.dto;

import dev.xeonkryptos.xeonrobotframeworkplugin.psi.element.KeywordFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public record KeywordFileWithDependentsWrapper(@NotNull KeywordFile keywordFile, @NotNull Collection<KeywordFile> dependents) {}
