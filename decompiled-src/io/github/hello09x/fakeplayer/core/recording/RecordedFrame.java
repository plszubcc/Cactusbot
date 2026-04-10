package io.github.hello09x.fakeplayer.core.recording;

public record RecordedFrame(int tick, double x, double y, double z, float yaw, float pitch, float forwardInput, float strafeInput, String heldItem, boolean attack, boolean useItem, boolean jump, boolean glide, boolean sprint, boolean sneak, Double targetDistance, Float targetAngle, Float targetPitchAngle) {

    public RecordedFrame {
        super();
    }
}
