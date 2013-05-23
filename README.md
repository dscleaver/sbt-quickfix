# sbt-quickfix

This is an sbt plugin that generates a quickfix file that can be used with Vim.

Inspired by [Alois Cochard's blog post] on setting up quickfix using a bash wrapper around sbt..

## Usage

Add this to your `~/.sbt/plugins/plugins.sbt`:

    addSbtPlugin("com.dscleaver.sbt" % "sbt-quickfix" % "0.1.0")

*Note: this plugin was built for sbt 0.12.*

## Vim Integration

If your vim is setup to use [Pathogen], then the vim-sbt plugin can be installed with this command:

    sbt install-vim-plugin

This command will replace the plugin files whenever it is run, so it is recommended when upgrading versions of the plugin.

### Using the files in Vim

After a compile simply hit `<Leader>ff` to jump to the first error and `<Leader>fn` to move to the next.

### Installation Location

By default the plugin will install to `~/.vim/bundle`. This plugin base directory can be changed before running install as follows:

    set QuickFixKeys.pluginBaseDirectory := new File("intended/directory") 

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
* Watch for test failures and write to file as well

[Alois Cochard's blog post]: http://aloiscochard.blogspot.co.uk/2013/02/quick-bug-fixing-in-scala-with-sbt-and.html
[Pathogen]: https://github.com/tpope/vim-pathogen
