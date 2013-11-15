package com.tlswe.example;

import java.io.Serializable;

import com.tlswe.awsmock.ec2.model.AbstractMockEc2Instance;

/**
 *
 * @author xma
 *
 */
public final class CustomMockEc2Instance extends AbstractMockEc2Instance {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;

    /**
     * A demo member variable.
     */
    private int foo;


    /**
     * Set value for foo.
     *
     * @param bar
     *            some value
     */
    public void setFoo(final int bar) {
        this.foo = bar;
    }


    @Override
    public void onStarted() {
        // do something
    }


    @Override
    public void onBooted() {
        switch (this.foo) {
        case 1:
            // do something
        case 2:
            // do something else
        default:
        }
    }


    @Override
    public void onStopping() {
        // do something
    }


    @Override
    public void onStopped() {
        // do something
    }


    @Override
    public void onTerminating() {
        switch (this.foo) {
        case 0:
            // do something
        case 1:
            // do something else
        default:
        }
    }


    @Override
    public void onTerminated() {
        // do something
    }


    @Override
    public void onInternalTimer() {
        // do something
    }

}
