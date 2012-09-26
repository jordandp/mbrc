package com.kelsos.mbrc.commands.model;

import com.google.inject.Inject;
import com.kelsos.mbrc.interfaces.ICommand;
import com.kelsos.mbrc.interfaces.IEvent;
import com.kelsos.mbrc.model.MainDataModel;
import com.kelsos.mbrc.views.MainView;
import com.kelsos.mbrc.controller.RunningActivityAccessor;

public class UpdateMainViewCommand implements ICommand
{
	@Inject
	private RunningActivityAccessor accessor;
	@Inject
	private MainDataModel model;

	public void execute(IEvent e)
	{
		if (accessor.getRunningActivity().getClass() != MainView.class) return;
		(accessor.getRunningActivity()).runOnUiThread(new Runnable()
		{
			public void run()
			{
				MainView view = (MainView) accessor.getRunningActivity();
				view.updateTitleText(model.getTitle());
				view.updateArtistText(model.getArtist());
				view.updateAlbumText(model.getAlbum());
				view.updateYearText(model.getYear());
				view.updateVolumeData(model.getVolume());
				if(model.getAlbumCover()!=null)
				{
					view.updateAlbumCover(model.getAlbumCover());
				}
				else
				{
					view.resetAlbumCover();
				}
				view.updateConnectionIndicator(model.getIsConnectionActive());
				view.updateRepeatButtonState(model.getIsRepeatButtonActive());
				view.updateShuffleButtonState(model.getIsShuffleButtonActive());
				view.updateScrobblerButtonState(model.getIsScrobbleButtonActive());
				view.updateMuteButtonState(model.getIsMuteButtonActive());
				view.updatePlayState(model.getPlayState());
			}
		});

	}
}