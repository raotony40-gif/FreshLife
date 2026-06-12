import UIAbility from '@ohos.app.ability.UIAbility';
import Want from '@ohos.app.ability.Want';
import window from '@ohos.window';
import hilog from '@ohos.hilog';

const DOMAIN: number = 0x0000;

export default class EntryAbility extends UIAbility {
  onCreate(want: Want): void {
    AppStorage.setOrCreate<Want>('want', want);
  }

  onWindowStageCreate(windowStage: window.WindowStage): void {
    windowStage.loadContent('pages/LoginPage', (err) => {
      if (err.code) {
        hilog.error(DOMAIN, 'FreshLife', 'Failed to load content: %{public}s', JSON.stringify(err));
        return;
      }
      hilog.info(DOMAIN, 'FreshLife', 'Succeeded in loading content.');
    });
  }
}
