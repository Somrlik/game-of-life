#include <stdio.h>
#include <stdbool.h>
#include <stdlib.h>
#include <signal.h>
#include <time.h>
#include <memory.h>

#define DISPLAY_ALIVE_CHAR '#'
#define DISPLAY_DEAD_CHAR ' '
#define DISPLAY_SEPARATOR_CHAR '-'

static volatile sig_atomic_t keep_running = true;

bool* map;
bool* temp_map;
long width, height;

void init(size_t, size_t);
void step();
void shutdown_handler(int);
void print_map();

bool get(long, long);
bool is_alive(long, long);

int main(int argc, char ** argv) {
    long width, height;

    if (argc != 3) {
        printf("Usage: with[int] height[int]\r\n");
        return 1;
    }

    width = atoi(argv[1]);
    height = atoi(argv[2]);

    if (width <= 0) {
        printf("width must be a positive integer, %s supplied.\r\n", argv[1]);
        return 1;
    }

    if (height <= 0) {
        printf("height must be a positive integer, %s supplied.\r\n", argv[2]);
        return 1;
    }

    init((size_t) width, (size_t) height);
    while (keep_running) {
        step();
        print_map();
#ifdef __linux__
        #include <unistd.h>
        usleep(50);
#elif _WIN32
        // Windows pause?
        #include <windows.h>
        Sleep(50);
#else
        // Cross-platform pause?
#endif
    }
}

char * lineBuffer;

void init(size_t width_, size_t height_) {
    width = width_;
    height = height_;

    map = calloc((size_t) (width * height), sizeof(bool));
    temp_map = malloc(width * height * sizeof(bool));

    lineBuffer = malloc((width + 1)  * sizeof(char));
    srand(time(0));

    for (int i = 0; i < width * height; ++i) {
        map[i] = rand() > (RAND_MAX / 2);
    }

//    map[width * 1 + 2] = 1;
//    map[width * 2 + 3] = 1;
//    map[width * 3 + 1] = 1;
//    map[width * 3 + 2] = 1;
//    map[width * 3 + 3] = 1;

    signal(SIGINT, shutdown_handler);
}

void step() {
    for (int i = 0; i < width; ++i) {
        for (int j = 0; j < height; ++j) {
            temp_map[j * width + i] = is_alive(i, j);
        }
    }
    memcpy(map, temp_map, width * height * sizeof(bool));
}

void print_map() {

    for (int i = 0; i < height; ++i) {
        for (int j = 0; j < width; ++j) {
            lineBuffer[j] = (char) (get(j, i) ? DISPLAY_ALIVE_CHAR : DISPLAY_DEAD_CHAR);
        }
        lineBuffer[width] = '\0';
        printf("%s\n\r", lineBuffer);
    }
    for (int k = 0; k < width; ++k) {
        lineBuffer[k] = DISPLAY_SEPARATOR_CHAR;
    }
    lineBuffer[width] = '\0';
    printf("%s\n\r", lineBuffer);
}

inline bool is_alive(long x, long y) {
    if (x < 0 || x >= width || y < 0 || y >= height) {
        return false;
    }

    int sum =
            get(x - 1, y - 1) +
            get(x    , y - 1) +
            get(x + 1, y - 1) +
            get(x - 1, y    ) +
            get(x + 1, y    ) +
            get(x - 1, y + 1) +
            get(x    , y + 1) +
            get(x + 1, y + 1) ;

    if (sum == 3) {
        return true;
    } else if (sum == 2) {
        return get(x, y);
    } else {
        return false;
    }
}

inline bool get(long x, long y) {
    if (x < 0 || x >= width || y < 0 || y >= height) return false;
    return map[y * width + x];
}

void shutdown_handler(int _) {
    (void)_;
    keep_running = false;
    free(map);
    free(temp_map);
    free(lineBuffer);
}
