
interface PollResume {
    (): void;
}

interface PollController {

    stopRefresh(): PollResume;
}

interface Pollable {

    poll(): void;
}