package server.sessions;

/**
 * Ключ каждой сессии это хэш отправителя и отправляющего
 */
public class ChatRoom {

    private int firstChatMemberId;

    private int secondChatMemberId;

    private boolean isOpen;

    public ChatRoom(int firstChatMemberId, int secondChatMemberId) {
        this.firstChatMemberId = firstChatMemberId;
        this.secondChatMemberId = secondChatMemberId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ChatRoom that = (ChatRoom) o;
        return hashCode() == that.hashCode();
    }


    @Override
    public int hashCode() {
        return (String.valueOf(firstChatMemberId) + String.valueOf(secondChatMemberId)).hashCode();
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public int getFirstChatMemberId() {
        return firstChatMemberId;
    }

    public void setFirstChatMemberId(int firstChatMemberId) {
        this.firstChatMemberId = firstChatMemberId;
    }

    public int getSecondChatMemberId() {
        return secondChatMemberId;
    }

    public void setSecondChatMemberId(int secondChatMemberId) {
        this.secondChatMemberId = secondChatMemberId;
    }
}
