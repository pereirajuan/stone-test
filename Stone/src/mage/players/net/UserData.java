package mage.players.net;

import java.io.Serializable;

/**
 * User data that is passed during connection to the server.
 *
 * @author ayrat
 */
public class UserData implements Serializable {

    protected int groupId;
    protected int avatarId;
    protected boolean showAbilityPickerForced;
    protected UserSkipPrioritySteps userSkipPrioritySteps;

    public UserData(UserGroup userGroup, int avatarId, boolean showAbilityPickerForced, UserSkipPrioritySteps userSkipPrioritySteps) {
        this.groupId = userGroup.getGroupId();
        this.avatarId = avatarId;
        this.showAbilityPickerForced = showAbilityPickerForced;
        this.userSkipPrioritySteps = userSkipPrioritySteps;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public int getAvatarId() {
        return avatarId;
    }

    public void setAvatarId(int avatarId) {
        this.avatarId = avatarId;
    }

    public boolean isShowAbilityPickerForced() {
        return showAbilityPickerForced;
    }

    public void setShowAbilityPickerForced(boolean showAbilityPickerForced) {
        this.showAbilityPickerForced = showAbilityPickerForced;
    }

    public UserSkipPrioritySteps getUserSkipPrioritySteps() {
        return userSkipPrioritySteps;
    }

    public void setUserSkipPrioritySteps(UserSkipPrioritySteps userSkipPrioritySteps) {
        this.userSkipPrioritySteps = userSkipPrioritySteps;
    }
    
}
