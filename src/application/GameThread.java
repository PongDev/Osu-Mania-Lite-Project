package application;

import java.util.ArrayList;

import config.Config;

public class GameThread extends Thread {

	private BeatMap beatMap;
	private NoteTiles noteTiles;

	public GameThread(BeatMap beatMap, NoteTiles noteTiles) {
		this.noteTiles = noteTiles;
		this.beatMap = beatMap;
	}

	public void run() {
		try {
			beatMap.playMusic();
			ArrayList<Integer> notePosition = new ArrayList<Integer>();

			for (int i = 0; i < Config.K_TILE; i++) {
				notePosition.add(0);
			}
			while (true) {
				Thread.sleep(Config.DELAY_BETWEEN_FRAME);
				int beatMapLoopMusicTimeStamp = beatMap.getCurrentMusicTimestamp();

				for (int i = 0; i < Config.K_TILE; i++) {
					int currentNotePosition = notePosition.get(i);
					while (beatMap.getBeatMapNote(i, currentNotePosition) != -1
							&& beatMapLoopMusicTimeStamp > beatMap.getBeatMapNote(i, currentNotePosition)) {
						currentNotePosition += 1;
						notePosition.set(i, currentNotePosition);
					}
					int lastCurrentNotePosition = currentNotePosition;
					while (beatMap.getBeatMapNote(i, lastCurrentNotePosition) != -1 && beatMapLoopMusicTimeStamp
							+ Config.TILE_TIME_RANGE > beatMap.getBeatMapNote(i, lastCurrentNotePosition)) {
						lastCurrentNotePosition += 1;
					}

					for (int j = 0; j < Config.SUBTILE_PER_TILE; j++) {
						boolean isSetTime = false;
						int currentMusicTimeForTile = beatMapLoopMusicTimeStamp + (j * Config.TIME_RANGE_PER_TILE);
						for (int k = currentNotePosition; k <= lastCurrentNotePosition; k++) {

							int notePositionTime = beatMap.getBeatMapNote(i, k);

							if (notePositionTime != -1 && currentMusicTimeForTile <= notePositionTime
									&& notePositionTime - currentMusicTimeForTile <= Config.TIME_RANGE_PER_TILE) {
								noteTiles.setTileColor(i, Config.SUBTILE_PER_TILE - 1 - j);
								isSetTime = true;
							}
						}
						if (!isSetTime) {
							noteTiles.resetTileColor(i, Config.SUBTILE_PER_TILE - 1 - j);
						}
					}
				}
			}
		} catch (Exception e) {
		}
	}
}
