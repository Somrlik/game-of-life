cmake_minimum_required(VERSION 3.13)
project(game-of-life C)

set(CMAKE_C_STANDARD 99)

add_executable(game_of_life main.c)

if(MSVC)
    target_compile_options(game_of_life PRIVATE /W4 /WX)
else()
    target_compile_options(game_of_life PRIVATE -Wall -Wextra -pedantic -Werror)
endif(MSVC)