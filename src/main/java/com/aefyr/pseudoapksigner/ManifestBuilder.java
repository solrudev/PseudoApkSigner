package com.aefyr.pseudoapksigner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

class ManifestBuilder {

    private final ArrayList<ManifestEntry> mEntries;
    private long mVersion = 0;
    private String mCachedManifest;
    private long mCachedVersion = -1;

    ManifestBuilder() {
        mEntries = new ArrayList<>();
    }

    String build() {
        if (mVersion == mCachedVersion) {
            return mCachedManifest;
        }
        var stringBuilder = new StringBuilder();
        stringBuilder.append(generateHeader());
        for (var entry : mEntries) {
            stringBuilder.append(entry.toString());
        }
        mCachedVersion = mVersion;
        mCachedManifest = stringBuilder.toString();
        return mCachedManifest;
    }

    private ManifestEntry generateHeader() {
        var header = new ManifestEntry();
        header.setAttribute("Manifest-Version", "1.0");
        header.setAttribute("Created-By", Constants.GENERATOR_NAME);
        return header;
    }

    static class ManifestEntry {
        private final LinkedHashMap<String, String> mAttributes;

        ManifestEntry() {
            mAttributes = new LinkedHashMap<>();
        }

        void setAttribute(String attribute, String value) {
            mAttributes.put(attribute, value);
        }

        String getAttribute(String attribute) {
            return mAttributes.get(attribute);
        }

        @Override
        public String toString() {
            var stringBuilder = new StringBuilder();
            for (var key : mAttributes.keySet()) {
                stringBuilder.append(String.format("%s: %s" + Constants.LINE_ENDING, key, mAttributes.get(key)));
            }
            stringBuilder.append(Constants.LINE_ENDING);
            return stringBuilder.toString();
        }
    }

    void addEntry(ManifestEntry entry) {
        mEntries.add(entry);
        mVersion++;
    }

    List<ManifestEntry> getEntries() {
        return mEntries;
    }
}
