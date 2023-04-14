package com.aefyr.pseudoapksigner;

class SignatureFileGenerator {

    private final ManifestBuilder mManifest;
    private final String mHashingAlgorithm;

    SignatureFileGenerator(ManifestBuilder manifestBuilder, String hashingAlgorithm) {
        mManifest = manifestBuilder;
        mHashingAlgorithm = hashingAlgorithm;
    }

    String generate() throws Exception {
        var stringBuilder = new StringBuilder();
        stringBuilder.append(generateHeader());
        for (var manifestEntry : mManifest.getEntries()) {
            var sfEntry = new ManifestBuilder.ManifestEntry();
            sfEntry.setAttribute("Name", manifestEntry.getAttribute("Name"));
            sfEntry.setAttribute(mHashingAlgorithm + "-Digest", Utils.base64Encode(Utils.hash(manifestEntry.toString().getBytes(Constants.UTF8), mHashingAlgorithm)));
            stringBuilder.append(sfEntry);
        }
        return stringBuilder.toString();
    }

    private ManifestBuilder.ManifestEntry generateHeader() throws Exception {
        var header = new ManifestBuilder.ManifestEntry();
        header.setAttribute("Signature-Version", "1.0");
        header.setAttribute("Created-By", Constants.GENERATOR_NAME);
        header.setAttribute(mHashingAlgorithm + "-Digest-Manifest", Utils.base64Encode(Utils.hash(mManifest.build().getBytes(Constants.UTF8), mHashingAlgorithm)));
        return header;
    }
}
