import java.io.File;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class Note {
    // this program creates a class Note with defined midi
    // and name of the instrument on which it is played
    // the number of notes in the octave
    private static final int NOTES_IN_OCTAVE = 12;
    // given frequency
    private static final double FREQUENCY = 440.0;
    // audio sampling rate for synthesized playback
    private static final int SAMPLING_RATE = 44100;
    // duration (in seconds) of a synthesized note
    private static final double TONE_DURATION = 0.8;
    // array of notes
    private static final String[] NOTES = {
            "C", "C#", "D", "D#", "E", "F",
            "F#", "G", "G#", "A", "A#", "B"
    };

    // instance variable midi
    private final int midi;
    // instance variable the name of the instrument
    private final String nameOfInstrument;


    // Creates a note with the given
    // MIDI number and instrument name
    public Note(int midiNumber, String instrumentName) {
        midi = midiNumber;
        nameOfInstrument = instrumentName;
    }

    // Returns this note's MIDI number.
    public int midi() {
        return midi;
    }

    // Returns this note's frequency.
    public double frequency() {
        double exponent = ((double) midi - 69.0) / NOTES_IN_OCTAVE;
        double frequency = FREQUENCY * Math.pow(2, exponent);
        return frequency;
    }

    // Plays this note to standard audio. If the associated WAV file
    // (e.g. piano/piano69.wav) exists, plays it; otherwise synthesizes
    // a tone at this note's frequency, so the class works without
    // any instrument sample files.
    public void play() {
        String audiofile = nameOfInstrument + "/" + nameOfInstrument + midi + ".wav";
        File file = new File(audiofile);
        if (file.exists()) playWavFile(file);
        else playTone(frequency(), TONE_DURATION);
    }

    // Plays a WAV file on standard audio, blocking until it finishes.
    // (Replacement for StdAudio.play().)
    private static void playWavFile(File file) {
        try (AudioInputStream audio = AudioSystem.getAudioInputStream(file)) {
            AudioFormat format = audio.getFormat();
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            byte[] buffer = new byte[4096];
            int count;
            while ((count = audio.read(buffer, 0, buffer.length)) != -1) {
                line.write(buffer, 0, count);
            }
            line.drain();
            line.close();
        }
        catch (Exception e) {
            throw new RuntimeException("could not play audio file: " + file, e);
        }
    }

    // Synthesizes a sine tone of the given frequency (Hz) and duration
    // (seconds) with a gentle decay, and plays it on standard audio.
    private static void playTone(double frequency, double duration) {
        try {
            AudioFormat format = new AudioFormat(SAMPLING_RATE, 16, 1, true, false);
            SourceDataLine line = AudioSystem.getSourceDataLine(format);
            line.open(format);
            line.start();
            int n = (int) (SAMPLING_RATE * duration);
            byte[] buffer = new byte[2 * n];
            for (int i = 0; i < n; i++) {
                double t = (double) i / SAMPLING_RATE;
                double envelope = Math.exp(-3.0 * t);      // gentle decay
                double sample = 0.5 * envelope * Math.sin(2 * Math.PI * frequency * t);
                short pcm = (short) (sample * Short.MAX_VALUE);
                buffer[2 * i] = (byte) pcm;
                buffer[2 * i + 1] = (byte) (pcm >> 8);
            }
            line.write(buffer, 0, buffer.length);
            line.drain();
            line.close();
        }
        catch (Exception e) {
            throw new RuntimeException("could not play tone", e);
        }
    }

    // Returns this note's name (e.g., C or A#).
    public String name() {
        return NOTES[midi % NOTES_IN_OCTAVE];
    }

    // Returns this note's octave.
    public int octave() {
        return midi / NOTES_IN_OCTAVE - 1;
    }

    // Returns a new Note transposed by delta semitones.
    public Note transpose(int delta) {
        int newMidi = midi + delta;
        Note transposedNote = new Note(newMidi, nameOfInstrument);
        return transposedNote;
    }

    // Returns a string representation of this note.
    public String toString() {
        String representation = midi + " "
                + name() + octave()
                + " (" + nameOfInstrument + ")";
        return representation;
    }

    // Unit tests the Note data type.
    public static void main(String[] args) {
        // taking midiNumber and instrument
        // as command-line arguments
        int midiNumber = Integer.parseInt(args[0]);
        String instrumentName = args[1];

        // also delta of transposition
        // as an argument
        int transposeDelta = Integer.parseInt(args[2]);

        // create new note
        Note createdNote = new Note(midiNumber, instrumentName);
        // print its midi, frequency, name and octave
        // as well as string representation
        System.out.println(createdNote.midi());
        System.out.println(createdNote.frequency());
        System.out.println(createdNote.name());
        System.out.println(createdNote.octave());
        System.out.println(createdNote.toString());

        // create a new transposed note, and print
        // all of the above as well
        Note transposedNote = createdNote.transpose(transposeDelta);

        System.out.println(transposedNote.midi());
        System.out.println(transposedNote.frequency());
        System.out.println(transposedNote.name());
        System.out.println(transposedNote.octave());
        System.out.println(transposedNote.toString());

        // finally, play the original note
        createdNote.play();

    }

}
