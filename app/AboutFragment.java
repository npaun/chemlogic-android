// AboutFragment.java: Displays copyright and version information for the Chemlogic app and enables access to some debugging features.
// This file is from Chemlogic, a logic programming computer chemistry system  
// <http://icebergsystems.ca/chemlogic>  
// (C) Copyright 2012-2015 Nicholas Paun  

package ca.nicholaspaun.chemlogic.app1;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class AboutFragment extends ChemlogicFragment {

	private ChemlogicController ctrl;

	public AboutFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_about, container,
				false);

		ctrl = ((MainActivity) getActivity()).getController();
		populate_about(rootView);
		populate_identity(rootView);

		setup_devToggle(rootView);
		setup_reset(rootView);
		setup_stop(rootView);
		setup_reinstall(rootView);

		// Be gone keyboard!
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);

		return rootView;
	}

	private String version_get() {
		String versionName = "unknown";

		try {
			versionName = getActivity().getPackageManager().getPackageInfo(
					getActivity().getPackageName(), 0).versionName;
			return ("<p><b>Version</b> " + versionName + "</p>");

		} catch (PackageManager.NameNotFoundException e) {
			return ("<p><b>Error!</b> Cannot determine version</p>");
		}

	}

	private void populate_about(View v) {
		TextView aboutText = (TextView) v.findViewById(R.id.about_copyright);
		aboutText.setText(Html.fromHtml(getString(R.string.about_copyright)
				+ version_get()));
		aboutText.setMovementMethod(LinkMovementMethod.getInstance());

	}

	private String fixTabs(String input) {
		return (input.replace("\t", " "));
	}

	private void populate_identity(View v) {
		TextView identityText = (TextView) v.findViewById(R.id.about_identity);
		identityText.setText(fixTabs(ctrl.identify()));
	}

	private void setup_devToggle(View v) {
		final TextView button = (TextView) v
				.findViewById(R.id.about_dev_toggle);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				v.getRootView().findViewById(R.id.about_dev_panel)
						.setVisibility(View.VISIBLE);
			}
		});
	}

	private void setup_reset(View v) {
		final Button button = (Button) v.findViewById(R.id.about_reset_prolog);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ctrl.reset();
				ctrl.acknowledge();
				populate_identity(v.getRootView());
			}
		});
	}

	private void setup_stop(View v) {
		final Button button = (Button) v.findViewById(R.id.about_stop_prolog);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ctrl.halt();
			}
		});
	}

	private void setup_reinstall(View v) {
		final Button button = (Button) v.findViewById(R.id.about_reinstall_app);
		button.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				((MainActivity) getActivity()).installer_install();
			}
		});
	}

}
