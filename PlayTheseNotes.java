import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PlayTheseNotes {
    // Reads a sequence of notes from filename and returns them
    // as an array, using the given instrument.
    public static Note[] read(String filename, String instrument) {
        // represent the file as a Scanner over it
        try (Scanner ourFile = new Scanner(new File(filename))) {
            // read the first integer, which is the length
            int length = ourFile.nextInt();
            // create the array of notes we will return
            Note[] arrayOfNotes = new Note[length];
            // populate the array with all the notes with the
            // given midis
            for (int i = 0; i < length; i++) {
                // read the midi and save it
                int midi = ourFile.nextInt();
                // we also have to read the next double, but
                // it will not be used
                // (parse via next() so it works in every locale)
                double duration = Double.parseDouble(ourFile.next());
                // specify each note
                arrayOfNotes[i] = new Note(midi, instrument);
            }
            return arrayOfNotes;
        }
        catch (IOException e) {
            throw new RuntimeException("could not read notes file: " + filename, e);
        }
    }

    // Takes three command-line arguments (a filename, an instrument name,
    // and a transposition amount delta), reads the notes; transposes them;
    // plays them on standard audio; and prints the transposed notes.
    public static void main(String[] args) {
        // put in filename, instrument
        // and Delta as an argument
        String filename = args[0];
        String instrumentName = args[1];
        int transpositionDelta = Integer.parseInt(args[2]);
        // initialise and create via the method read
        // the array of notes
        Note[] arrayOfTransposedNotes = read(filename, instrumentName);
        // transposes each note and update the array
        for (int i = 0; i < arrayOfTransposedNotes.length; i++) {
            arrayOfTransposedNotes[i] = arrayOfTransposedNotes[i].transpose(transpositionDelta);
        }
        // play the transposed array
        for (int i = 0; i < arrayOfTransposedNotes.length; i++) {
            arrayOfTransposedNotes[i].play();
        }
        // print the name and octave of the transposed array,
        // separated by single spaces and ending with a newline
        for (int i = 0; i < arrayOfTransposedNotes.length; i++) {
            if (i > 0) System.out.print(" ");
            System.out.print(arrayOfTransposedNotes[i].name() + arrayOfTransposedNotes[i].octave());
        }
        System.out.println();
    }
}
