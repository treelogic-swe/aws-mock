package com.tlswe.awsmock.ec2.model;

import java.io.Serializable;

/**
 * Default type of mock ec2 instance (extends {@link AbstractMockEc2Instance}).
 *
 * @author xma
 *
 */
public class DefaultMockEc2Instance extends AbstractMockEc2Instance {

    /**
     * Default serial version ID for this class which implements {@link Serializable}.
     *
     * @see Serializable
     */
    private static final long serialVersionUID = 1L;


    @Override
    public void onStarted() {
        // do nothing by default
    }


    @Override
    public void onBooted() {
        // do nothing by default
    }


    @Override
    public void onStopping() {
        // do nothing by default
    }


    @Override
    public void onStopped() {
        // do nothing by default
    }


    @Override
    public void onTerminating() {
        // do nothing by default
    }


    @Override
    public void onTerminated() {
        // do nothing by default
    }


    @Override
    public void onInternalTimer() {
        // do nothing by default
    }

}
