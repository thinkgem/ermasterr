# ermasterr
A fork of ERMaster. Faster, Better, VCS-friendly.

Original version is http://ermaster.sourceforge.net  
> ERMaster is GUI editor for ER diagram.  
> It runs as Eclipse plug-in.  
> It can be done graphically to making ER diagram, printing ER diagram, exporting the DDL from ER diagram, etc. .  
> Moreover, importing from DB, management of the group, and the historical management, etc. are supported.  


# Install

Help -> Install New Software -> 

Update Site: http://thinkgem.gitee.io/ermasterr

# Install

1. Install Eclipse  
Package: `Eclipse IDE for Java Developers`

2. Install PDE plug-in  
Plug-in name: `Eclipse PDE Plug-in Developer Resources`

3. Install ermasterr  
Update Site: `http://thinkgem.gitee.io/ermasterr`
or
Copy the contents of the [dropins folder](https://gitee.com/thinkgem/ermasterr/tree/master/dropins/ermasterr/eclipse/plugins) to the dropins folder located in the root of your Eclipse installation.

Directory tree example:

```
your_eclipse_install_dir/
└── dropins/
    └── ermasterr/
        └── eclipse/
            └── plugins/
                └── org.insightech.er_x.y.z.jar
```

or put jar file to dropins directly.

```
your_eclipse_install_dir/
└── dropins/
    └── org.insightech.er_x.y.z.jar
```

# Features

## Faster
* ermasterr prevented a increase of the drawing time by getting rid of tabs of each category and skipping expensive initialization for each category.

## Better
* ermasterr has fixed the issue that erdiagram data file size becomes gigantic in sometimes by writing a large amount of same xml tags. (https://sourceforge.net/p/ermaster/bugs/119/)

## VCS-friendly
* ermasterr can write git-mergable erdiagram data as far as possible. (https://sourceforge.net/p/ermaster/feature-requests/104/)

# License
Apache License V2.0
