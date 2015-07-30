/* Copyright (C) 2014 Covisint. All Rights Reserved. */

package com.covisint.platform.clog.server.cloginstance;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

/** Class for printing out this library's version information. */
public final class LibInfo {

    /** Classpath location of the MANIFEST file. */
    private static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";

    /** Manifest entry name for Maven related attributes. */
    private static final String MVN_ENTRY = "Maven";

    /** Name of attribute containing artifact group ID. */
    private static final String MVN_ART_GRP = "ArtifactGroup";

    /** Name of attribute containing artifact ID. */
    private static final String MVN_ART_ID = "ArtifactId";

    /** Name of attribute containing artifact version. */
    private static final String MVN_ART_VER = "ArtifactVersion";

    /** Manifest entry name for SCM related attributes. */
    private static final String SCM_ENTRY = "SCM";

    /** Name of attribute containing repository URL. */
    private static final String SCM_REPO = "Repository";

    /** Name of attribute containing repository path. */
    private static final String SCM_PATH = "Path";

    /** Name of attribute containing revision. */
    private static final String SCM_REV = "Revision";

    /** Name of attribute containing boolean indicating local, uncommitted changes. */
    private static final String SCM_UNC_CHNG = "UncommitedChanges";

    /** Manifest entry name for build related attributes. */
    private static final String BLD_ENTRY = "Build";

    /** Name of attribute containing OS name. */
    private static final String BLD_OS_NAME = "OsName";

    /** Name of attribute containing OS version. */
    private static final String BLD_OS_VER = "OsVersion";

    /** Name of attribute containing OS architecture. */
    private static final String BLD_OS_ARCH = "OsArch";

    /** Name of attribute containing Java vendor. */
    private static final String BLD_J_VEND = "JavaVendor";

    /** Name of attribute containing Java version. */
    private static final String BLD_J_VER = "JavaVersion";

    /** Name of attribute containing build instant. */
    private static final String BLD_INSTANT = "BuildInstant";

    /** Name of attribute containing the builder. */
    private static final String BLD_BLDR = "Builder";

    /** The manifest for the jar this class is contained in. */
    private static Manifest jarManifest;

    /** Constructor. */
    private LibInfo() {

    }

    /**
     * Main entry point to program.
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        System.out.println("Maven Information:");
        System.out.println("  Group:     " + getMavenGroupId());
        System.out.println("  Artifact:  " + getMavenArtifactId());
        System.out.println("  Version:   " + getMavenArtifactVersion());

        System.out.println();

        System.out.println("SCM Information:");
        System.out.println("  Repository:  " + getScmRepository());
        System.out.println("  Path:        " + getScmPath());
        System.out.println("  Revision:    " + getScmRevision());
        System.out.println("  Uncommited:  " + getScmHasUncommitedChanges());

        System.out.println();

        System.out.println("Build Information:");
        System.out.println("  OS Name:          " + getBuildOsName());
        System.out.println("  OS Version:       " + getBuildOsVersion());
        System.out.println("  OS Architecture:  " + getBuildOsArchitecture());
        System.out.println("  Java Vendor:      " + getBuildJavaVendor());
        System.out.println("  Java Version:     " + getBuildJavaVersion());
        System.out.println("  Build Instant:    " + getBuildInstant());
        System.out.println("  Builder:          " + getBuilder());
    }

    /**
     * Gets the group ID of this Maven artifact.
     * 
     * @return group ID of this Maven artifact
     */
    public static String getMavenGroupId() {
        return jarManifest.getAttributes(MVN_ENTRY).getValue(MVN_ART_GRP);
    }

    /**
     * Gets the artifact ID of this Maven artifact.
     * 
     * @return artifact ID of this Maven artifact
     */
    public static String getMavenArtifactId() {
        return jarManifest.getAttributes(MVN_ENTRY).getValue(MVN_ART_ID);
    }

    /**
     * Gets the version of this Maven artifact.
     * 
     * @return version of this Maven artifact
     */
    public static String getMavenArtifactVersion() {
        return jarManifest.getAttributes(MVN_ENTRY).getValue(MVN_ART_VER);
    }

    /**
     * Gets the SCM repository from which the library was checked out.
     * 
     * @return the SCM repository from which the library was checked out
     */
    public static String getScmRepository() {
        return jarManifest.getAttributes(SCM_ENTRY).getValue(SCM_REPO);
    }

    /**
     * Gets the SCM repository path from which the library was checked out.
     * 
     * @return the SCM repository path from which the library was checked out
     */
    public static String getScmPath() {
        return jarManifest.getAttributes(SCM_ENTRY).getValue(SCM_PATH);
    }

    /**
     * Gets the SCM revision for the library.
     * 
     * @return the SCM revision for the library
     */
    public static String getScmRevision() {
        return jarManifest.getAttributes(SCM_ENTRY).getValue(SCM_REV);
    }

    /**
     * Gets whether the library was built with uncommitted changes.
     * 
     * @return whether the library was built with uncommitted changes
     */
    public static boolean getScmHasUncommitedChanges() {
        return Boolean.valueOf(jarManifest.getAttributes(SCM_ENTRY).getValue(SCM_UNC_CHNG));
    }

    /**
     * Gets the name of the operating system upon which library was built.
     * 
     * @return name of the operating system upon which library was built
     */
    public static String getBuildOsName() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_OS_NAME);
    }

    /**
     * Gets the version of the operating system upon which library was built.
     * 
     * @return version of the operating system upon which library was built
     */
    public static String getBuildOsVersion() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_OS_VER);
    }

    /**
     * Gets the architecture of the operating system upon which library was built.
     * 
     * @return architecture of the operating system upon which library was built
     */
    public static String getBuildOsArchitecture() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_OS_ARCH);
    }

    /**
     * Gets the vendor of the Java environment used to build the library.
     * 
     * @return vendor of the Java environment used to build the library
     */
    public static String getBuildJavaVendor() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_J_VEND);
    }

    /**
     * Gets the version of the Java environment used to build the library.
     * 
     * @return version of the Java environment used to build the library
     */
    public static String getBuildJavaVersion() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_J_VER);
    }

    /**
     * Gets the time the build was begun.
     * 
     * @return the time the build was begun
     */
    public static String getBuildInstant() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_INSTANT);
    }

    /**
     * Gets the user that executed the build.
     * 
     * @return the user that executed the build
     */
    public static String getBuilder() {
        return jarManifest.getAttributes(BLD_ENTRY).getValue(BLD_BLDR);
    }

    /** Gets a reference to the Manifest file for this jar. */
    static {
        try {
            final Enumeration<URL> resources = LibInfo.class.getClassLoader().getResources(MANIFEST_PATH);
            while (resources.hasMoreElements()) {
                final Manifest manifest = new Manifest(resources.nextElement().openStream());

                final Attributes entryAttributes = manifest.getEntries().get(MVN_ENTRY);
                if (entryAttributes != null
                        && LibInfo.class.getPackage().getName().equals(entryAttributes.getValue(MVN_ART_GRP))) {
                    jarManifest = manifest;
                    break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}