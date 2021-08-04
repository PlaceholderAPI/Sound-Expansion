package com.extendedclip.papi.expansion.sound;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import me.clip.placeholderapi.expansion.Cacheable;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class SoundExpansion extends PlaceholderExpansion implements Cacheable {

	private final Map<String, CachedSound> sounds = new ConcurrentHashMap<String, CachedSound>();
	
	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return "clip";
	}

	@Override
	public String getIdentifier() {
		return "sound";
	}

	@Override
	public String getPlugin() {
		return null;
	}

	@Override
	public String getVersion() {
		return "1.0.0";
	}

	@Override
	public void clear() {
		sounds.clear();
	}

	@Override
	public String onPlaceholderRequest(Player p, String identifier) {
		
		if (p == null) {
			return "";
		}
		
		boolean announce = false;
		
		if (identifier.startsWith("all_")) {
			announce = true;
			identifier = identifier.replace("all_", "");
		}
		
		if (sounds.containsKey(identifier)) {
			if (announce) {
				sounds.get(identifier).play();
			} else {
				sounds.get(identifier).play(p);
			}
			return "";
		}
		
		int first = identifier.indexOf("-");
		
		Sound s = null;
		
		if (first == -1) {
			try {
				String sound = identifier.toUpperCase();
				s = Sound.valueOf(sound);
				CachedSound cs = new CachedSound(s, 10, 1);
				sounds.put(identifier, cs);
				if (announce) {
					cs.play();
				} else {
					cs.play(p);
				}
				return "";
			} catch (Exception ex) {
				return null;
			}
		} else {
			try {
				String sound = identifier.substring(0, first).toUpperCase();
				s = Sound.valueOf(sound);
			} catch (Exception ex) {
				return null;
			}
		}
		
		if (s == null) {
			return null;
		}

		int volume = 10;
		
		int pitch = 1;
		
		String volPitch = identifier.substring(first+1);
		
		int second = volPitch.indexOf("-");
		
		if (second >= 1) {
			try {
				volume = Integer.parseInt(volPitch.substring(0, second));
			} catch (Exception ex) {
			}
			
			try {
				pitch = Integer.parseInt(volPitch.substring(second+1));
			} catch (Exception ex) {
			}
		}
		
		CachedSound cs = new CachedSound(s, volume, pitch);
		sounds.put(identifier, cs);
		if (announce) {
			cs.play();
		} else {
			cs.play(p);
		}
		return "";
	}

	private class CachedSound {

		private Sound s;
		private int volume;
		private int pitch;
		
		public CachedSound(Sound s, int volume, int pitch) {
			this.s = s;
			this.volume = volume;
			this.pitch = pitch;
		}
		
		public void play() {
			for (Player p : Bukkit.getOnlinePlayers()) {
				play(p);
			}
		}
		public void play(Player p) {
			p.playSound(p.getLocation(), s, volume, pitch);
		}
	}
	

}
