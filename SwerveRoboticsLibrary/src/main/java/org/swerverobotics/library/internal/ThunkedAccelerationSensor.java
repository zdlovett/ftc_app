package org.swerverobotics.library.internal;

import com.qualcomm.robotcore.hardware.*;

/**
 * An AccelerationSensor that can be called on the main() thread.
 */
public class ThunkedAccelerationSensor extends AccelerationSensor
    {
    //----------------------------------------------------------------------------------------------
    // State
    //----------------------------------------------------------------------------------------------

    public AccelerationSensor target;   // can only talk to him on the loop thread

    //----------------------------------------------------------------------------------------------
    // Construction
    //----------------------------------------------------------------------------------------------

    private ThunkedAccelerationSensor(AccelerationSensor target)
        {
        if (target == null) throw new NullPointerException("null " + this.getClass().getSimpleName() + " target");
        this.target = target;
        }

    static public ThunkedAccelerationSensor create(AccelerationSensor target)
        {
        return target instanceof ThunkedAccelerationSensor ? (ThunkedAccelerationSensor)target : new ThunkedAccelerationSensor(target);
        }

    //----------------------------------------------------------------------------------------------
    // HardwareDevice
    //----------------------------------------------------------------------------------------------
    
    @Override public void close()
        {
        (new ThunkForWriting()
            {
            @Override protected void actionOnLoopThread()
                {
                target.close();
                }
            }).doWriteOperation();
        }
    
    @Override public int getVersion()
        {
        return (new ThunkForReading<Integer>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.getVersion();
                }
            }).doReadOperation();
        }

    @Override public String getConnectionInfo()
        {
        return (new ThunkForReading<String>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.getConnectionInfo();
                }
            }).doReadOperation();
        }

    @Override public String getDeviceName()
        {
        return (new ThunkForReading<String>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.getDeviceName();
                }
            }).doReadOperation();
        }
    
    //----------------------------------------------------------------------------------------------
    // AccelerationSensor
    //----------------------------------------------------------------------------------------------

    @Override public AccelerationSensor.Acceleration getAcceleration()
        {
        return (new ThunkForReading<Acceleration>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.getAcceleration();
                }
            }).doReadOperation();
        }

    @Override public String status()
        {
        return (new ThunkForReading<String>()
            {
            @Override protected void actionOnLoopThread()
                {
                this.result = target.status();
                }
            }).doReadOperation();
        }
    }
