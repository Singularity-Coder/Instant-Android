package com.singularitycoder.rooming;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Rule;

public class MovieDaoTest extends MovieDatabaseTest {

    @Rule
    public InstantTaskExecutorRule rule = new InstantTaskExecutorRule();

}