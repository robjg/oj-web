
interface PollResume {
    (): void;
}

interface PollController {

    stopRefresh(): PollResume;
}

