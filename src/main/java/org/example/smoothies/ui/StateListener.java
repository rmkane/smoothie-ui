package org.example.smoothies.ui;

import org.example.smoothies.ui.state.AppState;

@FunctionalInterface
public interface StateListener {

    void onStateChanged(AppState state);
}
