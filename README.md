# sbt-quickfix

This is an sbt plugin that generates a quickfix file that can be used with Vim.

Inspired by [Alois Cochard's blog post] on setting up quickfix using a bash wrapper around sbt..

## Usage

Add this to your `~/.sbt/plugins/plugins.sbt`:

    addSbtPlugin("com.dscleaver.sbt" % "sbt-quickfix" % "0.3.1")

*Note: this plugin was built for sbt 0.12+.*

## Vim Integration

If your vim is setup to use [Pathogen], then the vim-sbt plugin can be installed with this command:

    sbt install-vim-plugin

This command will replace the plugin files whenever it is run, so it is recommended when upgrading versions of the plugin.

### Using the files in Vim

After a compile or test, assuming there was an error, a quickfix file will be created that Vim can use.

If your copy of Vim was compiled with [+clientserver] and you've enabled the `vim-enable-server` flag in your SBT project (default is `true`), then this plugin will automatically load the quickfix file into Vim once it's been created.

If you don't have [+clientserver] enabled in Vim, or you have disabled the `vim-enable-server` feature of the plugin, then you can use a Vim mapping to load the file.  By default the mapping is `<leader>ff`, but you can change this if you wish.

### Installation Location

By default the plugin will install to `~/.vim/bundle`. This plugin base directory can be added to your global config as follows:

   QuickFixKeys.pluginBaseDirectory := file("intended/directory") 

### Previous Integration

The initial version of this plugin required that you add the following lines to your `~/.vimrc`:

    set errorformat=%E\ %#[error]\ %#%f:%l:\ %m,%-Z\ %#[error]\ %p^,%-C\ %#[error]\ %m
    set errorformat+=,%W\ %#[warn]\ %#%f:%l:\ %m,%-Z\ %#[warn]\ %p^,%-C\ %#[warn]\ %m
    set errorformat+=,%-G%.%#

    noremap <silent> <Leader>ff :cf target/quickfix/sbt.quickfix<CR>
    noremap <silent> <Leader>fn :cn<CR>

These lines should be removed from `~/.vimrc'

## To Do 

Items that I want to look into in the future:

* Possibly reformat log output for easier consumption
* Add ctags generation possibly turning this into a full vim plugin
* Allow vim server name to be overridden by system property

[Alois Cochard's blog post]: http://aloiscochard.blogspot.co.uk/2013/02/quick-bug-fixing-in-scala-with-sbt-and.html
[Pathogen]: https://github.com/tpope/vim-pathogen
[+clientserver]: http://vimhelp.appspot.com/remote.txt.html#clientserver
