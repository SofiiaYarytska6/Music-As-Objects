# Notes — music as objects in Java

 **Learning project.** A small object-oriented model of musical notes: an
immutable `Note` data type built around its MIDI number, and a player that
reads a song from a file, transposes it by any number of semitones, and plays
it. Built while learning how to design data types in Java — my favorite
discovery was that transposition is just `midi + delta`: all the messy musical
notation (sharps, note names, octaves) is *derived*, and the integer
underneath is the single source of truth.

## How it works

- **`Note.java`** — an immutable note defined by a MIDI number and an
  instrument name. It can report its frequency (440 × 2^((m−69)/12) Hz), its
  scientific pitch name (`69` → `A4`, computed with modular arithmetic and a
  12-name lookup array — no if-ladders), and produce a *new* note transposed
  by any number of semitones. `play()` uses `javax.sound.sampled`: if an
  instrument sample exists (e.g. `piano/piano69.wav`), it plays that WAV;
  otherwise it synthesizes a sine tone at the note's frequency with a gentle
  decay — so everything works with no sound files at all.
- **`PlayTheseNotes.java`** — the client. Reads a song file (a note count,
  then MIDI-number/duration pairs), transposes every note by a Δ given on the
  command line, plays each one, and prints the transposed pitches on one line.

## Dependencies

None — only the Java standard library (`java.util.Scanner`,
`javax.sound.sampled`). Requires a JDK (tested with Java 21) and a sound card.

## Compiling and running

```bash
javac Note.java PlayTheseNotes.java

# unit test: create A4, inspect it, transpose it up 8 semitones, play it
java Note 69 piano 8

# play a song: filename, instrument, transposition in semitones
java PlayTheseNotes C4-major-scale.txt piano 0
java PlayTheseNotes C4-major-scale.txt piano 8
```

Sample output:

```
$ java PlayTheseNotes C4-major-scale.txt piano 0
C4 D4 E4 F4 G4 A4 B4 C5

$ java PlayTheseNotes C4-major-scale.txt piano 8
G#4 A#4 C5 C#5 D#5 F5 G5 G#5
```

## Song file format

```
8
60 1.0
62 1.0
64 1.0
...
```

First line: the number of notes. Each remaining line: a MIDI number and a
duration in seconds. Write your own songs — any melody is just a list of
integers. To use real instrument sounds instead of synthesized tones, place
WAV samples in a folder named after the instrument, one file per note
(`piano/piano60.wav`, `piano/piano61.wav`, ...).

## What I learned

- Designing an immutable data type: `transpose()` returns a new `Note`
  instead of modifying the original, which makes the client code impossible
  to break with aliasing bugs
- The exponential structure of pitch: each semitone multiplies frequency by
  2^(1/12), so equal steps in *music* are equal ratios in *physics*
- Basic audio synthesis with `javax.sound.sampled`: generating a sine wave
  sample by sample, shaping it with a decay envelope, and converting to
  16-bit PCM
