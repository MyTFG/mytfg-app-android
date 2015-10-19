package de.mytfg.app.android.modules.vplan.objects;

import java.util.List;

public class VplanInfo extends VplanObject {
    private List<String> infos;

    public VplanInfo(List<String> infos) {
        this.infos = infos;
    }

    public List<String> getInfos() {
        return infos;
    }
}
