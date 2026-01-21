package dev.xeonkryptos.xeonrobotframeworkplugin.util;

import com.intellij.openapi.Disposable;

import java.util.function.Supplier;

public interface DisposableSupplier<T> extends Supplier<T>, Disposable {}
