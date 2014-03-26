/*
 * Autopsy Forensic Browser
 *
 * Copyright 2014 Basis Technology Corp.
 * Contact: carrier <at> sleuthkit <dot> org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.sleuthkit.autopsy.hashdatabase;

import java.util.ArrayList;
import java.util.List;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.sleuthkit.autopsy.coreutils.Version;
import org.sleuthkit.autopsy.ingest.IngestModuleFactoryAdapter;
import org.sleuthkit.autopsy.ingest.FileIngestModule;
import org.sleuthkit.autopsy.ingest.IngestModuleFactory;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettings;
import org.sleuthkit.autopsy.ingest.IngestModuleIngestJobSettingsPanel;
import org.sleuthkit.autopsy.ingest.IngestModuleGlobalSetttingsPanel;

/**
 * A factory that creates file ingest modules that do hash database lookups.
 */
@ServiceProvider(service = IngestModuleFactory.class)
public class HashLookupModuleFactory extends IngestModuleFactoryAdapter {

    private HashLookupModuleSettingsPanel moduleSettingsPanel = null;

    @Override
    public String getModuleDisplayName() {
        return getModuleName();
    }

    static String getModuleName() {
        return NbBundle.getMessage(HashDbIngestModule.class, "HashDbIngestModule.moduleName");
    }

    @Override
    public String getModuleDescription() {
        return NbBundle.getMessage(HashDbIngestModule.class, "HashDbIngestModule.moduleDescription");
    }

    @Override
    public String getModuleVersionNumber() {
        return Version.getVersion();
    }

    @Override
    public IngestModuleIngestJobSettings getDefaultModuleSettings() {
        // All available hash sets are enabled by default.
        HashDbManager hashDbManager = HashDbManager.getInstance();
        List<String> knownHashSetNames = getHashSetNames(hashDbManager.getKnownFileHashSets());
        List<String> knownBadHashSetNames = getHashSetNames(hashDbManager.getKnownBadFileHashSets());
        return new HashLookupModuleSettings(hashDbManager.getAlwaysCalculateHashes(), knownHashSetNames, knownBadHashSetNames);
    }

    private List<String> getHashSetNames(List<HashDbManager.HashDb> hashDbs) {
        List<String> hashSetNames = new ArrayList<>();
        for (HashDbManager.HashDb db : hashDbs) {
            hashSetNames.add(db.getHashSetName());
        }
        return hashSetNames;
    }

    @Override
    public boolean hasModuleSettingsPanel() {
        return true;
    }

    @Override
    public IngestModuleIngestJobSettingsPanel getModuleSettingsPanel(IngestModuleIngestJobSettings settings) {
        if (!(settings instanceof HashLookupModuleSettings)) {
            throw new IllegalArgumentException("Expected settings argument to be instanceof HashLookupModuleSettings");
        }
        if (moduleSettingsPanel == null) {
            moduleSettingsPanel = new HashLookupModuleSettingsPanel((HashLookupModuleSettings) settings);
        } else {
            moduleSettingsPanel.reset((HashLookupModuleSettings) settings);
        }
        return moduleSettingsPanel;
    }

    @Override
    public boolean hasGlobalSettingsPanel() {
        return true;
    }

    @Override
    public IngestModuleGlobalSetttingsPanel getGlobalSettingsPanel() {
        HashLookupSettingsPanel globalSettingsPanel = new HashLookupSettingsPanel();
        globalSettingsPanel.load();
        return globalSettingsPanel;
    }

    @Override
    public boolean isFileIngestModuleFactory() {
        return true;
    }

    @Override
    public FileIngestModule createFileIngestModule(IngestModuleIngestJobSettings settings) {
        if (!(settings instanceof HashLookupModuleSettings)) {
            throw new IllegalArgumentException("Expected settings argument to be instanceof HashLookupModuleSettings");
        }
        return new HashDbIngestModule((HashLookupModuleSettings) settings);
    }
}