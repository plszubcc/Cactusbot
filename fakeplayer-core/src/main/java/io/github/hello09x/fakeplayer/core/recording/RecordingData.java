package io.github.hello09x.fakeplayer.core.recording;

import java.util.List;

public record RecordingData(String name, String sourcePlayer, long recordedAt, int sampleCount, List<RecordedFrame> frames) {
}
